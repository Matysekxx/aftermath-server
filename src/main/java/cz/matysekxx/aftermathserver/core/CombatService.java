package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Inventory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service responsible for handling combat mechanics.
 * <p>
 * Manages player attacks, damage calculation, and NPC death processing.
 *
 * @author Matysekxx
 */
@Service
@Slf4j
public class CombatService {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;

    /**
     * Constructs the CombatService.
     *
     * @param worldManager     The manager for world data.
     * @param gameEventQueue   The queue for game events.
     * @param mapObjectFactory Factory for creating map objects (loot bags).
     */
    public CombatService(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
    }

    /**
     * Processes an attack initiated by a player.
     * <p>
     * Checks if the player has a weapon equipped, finds the nearest valid target
     * within range using spatial indexing, applies damage, and handles target death if necessary.
     *
     * @param player The player performing the attack.
     */
    public void handleAttack(Player player) {
        final Integer equippedSlot = player.getEquippedWeaponSlot();
        if (equippedSlot == null) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You don't have any weapon equipped!", player.getId()));
            return;
        }

        final Inventory inv = player.getInventory();
        final Item weapon = inv.getSlots().get(equippedSlot);

        if (weapon == null || weapon.getType() != ItemType.WEAPON) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Equipped item is not a valid weapon", player.getId()));
            return;
        }

        final int weaponCooldown = weapon.getCooldown() != null ? weapon.getCooldown() : 1000;
        final long currentTime = System.currentTimeMillis();
        if (currentTime - player.getLastAttackTime() < weaponCooldown) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You are attacking too quickly!", player.getId()));
            return;
        }

        final int weaponRange = weapon.getRange() != null ? weapon.getRange() : 1;

        final GameMapData map = worldManager.getMap(player.getMapId());
        final Npc closestNpc = map.getNpcs().stream()
                .filter(n -> n.getLayerIndex() == player.getLayerIndex())
                .filter(n -> !n.isDead())
                .filter(n -> MathUtil.getChebyshevDistance(
                        Vector2.of(player.getX(), player.getY()),
                        Vector2.of(n.getX(), n.getY())) <= weaponRange
                ).min(Comparator.comparingInt(n -> MathUtil.getChebyshevDistance(
                        Vector2.of(player.getX(), player.getY()),
                        Vector2.of(n.getX(), n.getY())
                )))
                .orElse(null);

        if (closestNpc == null) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Target not found", player.getId()));
            return;
        }

        final int damage = weapon.getDamage() != null ? weapon.getDamage() : 1;
        closestNpc.takeDamage(damage);
        player.setLastAttackTime(System.currentTimeMillis());
        log.info("Player {} dealt {} damage to NPC {}", player.getName(), damage, closestNpc.getName());
        if (closestNpc.isDead()) handleNpcDeath(closestNpc, map, player.getId());
        else {
            final List<NpcDto> update = List.of(NpcDto.fromEntity(closestNpc));
            gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(update, map.getId()));
        }
    }

    /**
     * Handles the logic when an NPC dies.
     * <p>
     * Removes the NPC from the map, spawns loot, and broadcasts updates to players.
     *
     * @param npc      The NPC that died.
     * @param map      The map where the death occurred.
     * @param killerId The ID of the player who killed the NPC.
     */
    private void handleNpcDeath(Npc npc, GameMapData map, String killerId) {
        map.getNpcs().remove(npc);
        if (npc.getLoot() != null) {
            for (Item item : npc.getLoot()) {
                final MapObject lootBag = mapObjectFactory.createLootBag(item.getId(), item.getQuantity(), npc.getX(), npc.getY(), npc.getLayerIndex());
                map.addObject(lootBag);
            }
        }
        gameEventQueue.enqueue(GameEventFactory.broadcastMapObjects(map.getObjects(), map.getId()));

        final List<NpcDto> remainingNpcs = map.getNpcs().stream().map(NpcDto::fromEntity).toList();
        gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(remainingNpcs, map.getId()));

        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("You killed " + npc.getName(), killerId));

        final boolean hasAggressiveNpcs = map.getNpcs()
                .stream().anyMatch(Npc::isAggressive);
        if (!hasAggressiveNpcs && !map.isCleared()) {
            map.setCleared(true);
            final var announcement = "STATION SECURED: " + map.getName() + " has been liberated by " + killerId + "!";
            gameEventQueue.enqueue(GameEventFactory.broadcastGlobalAnnouncement(announcement));
            log.info("Map {} has been cleared by player {}", map.getId(), killerId);
            checkTotalVictory();
        }
    }

    private void checkTotalVictory() {
        boolean allCleared = worldManager.getMaps().stream()
                .filter(m -> m.getType() == MapType.HAZARD_ZONE)
                .allMatch(GameMapData::isCleared);
        if (allCleared) {
            gameEventQueue.enqueue(GameEventFactory.broadcastGlobalAnnouncement(
                "!!! TOTAL VICTORY !!! The entire Metro system is now safe. Humanity has reclaimed its home. " +
                "All survivors are requested to report to their local Administrators to finalize their debts."
            ));
        }
    }
}