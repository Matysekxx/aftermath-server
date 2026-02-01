package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.entity.Inventory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Vector2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CombatService {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final SpatialService spatialService;

    public CombatService(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, SpatialService spatialService) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.spatialService = spatialService;
    }

    public void handleAttack(Player player, AttackRequest attackRequest) {
        final GameMapData map = worldManager.getMap(player.getMapId());
        if (map == null) return;

        final Optional<Npc> closestEntity = map.getNpcs().stream()
                .filter(e -> e.getId().equals(attackRequest.getTargetId()))
                .findFirst();
        if (closestEntity.isEmpty()) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Target not found", player.getId()));
            return;
        }

        final Npc target = closestEntity.get();
        final int distance = MathUtil.getChebyshevDistance(
                Vector2.of(player.getX(), player.getY()),
                Vector2.of(target.getX(), target.getY())
        );

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

        final int weaponRange = weapon.getRange() != null ? weapon.getRange() : 1;
        if (distance > weaponRange) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Weapon range exceeded", player.getId()));
            return;
        }

        target.takeDamage(weapon.getDamage());
        log.info("Player {} dealt {} damage to NPC {}", player.getName(), weapon.getDamage(), target.getName());

        if (target.isDead()) handleNpcDeath(target, map, player.getId());
        else {
            final List<NpcDto> update = List.of(NpcDto.fromEntity(target));
            gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(update, map.getId()));
        }
    }

    private void handleNpcDeath(Npc npc, GameMapData map, String killerId) {
        map.getNpcs().remove(npc);
        if (npc.getLoot() != null) {
            for (Item item : npc.getLoot()) {
                final MapObject lootBag = mapObjectFactory.createLootBag(item.getId(), item.getQuantity(), npc.getX(), npc.getY());
                map.addObject(lootBag);
            }
        }
        gameEventQueue.enqueue(GameEventFactory.broadcastMapObjects(map.getObjects(), map.getId()));

        final List<NpcDto> remainingNpcs = map.getNpcs().stream().map(NpcDto::fromEntity).toList();
        gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(remainingNpcs, map.getId()));

        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("You killed " + npc.getName(), killerId));
    }
}