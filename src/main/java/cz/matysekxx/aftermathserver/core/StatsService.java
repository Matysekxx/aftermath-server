package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.logic.items.ItemEffect;
import cz.matysekxx.aftermathserver.core.model.entity.Inventory;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.UseRequest;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service responsible for managing and applying periodic changes to player statistics.
 * <p>
 * This service handles environmental effects such as radiation damage in hazard zones
 * and health regeneration in safe zones.
 *
 * @author Matysekxx
 */
@Service
public class StatsService {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final Map<String, ItemEffect> itemEffects;

    /**
     * Constructs the StatsService.
     *
     * @param worldManager   The world manager to access map data.
     * @param gameEventQueue The event queue for sending updates.
     * @param itemEffects    A map of item effects for consumables.
     */
    public StatsService(WorldManager worldManager, GameEventQueue gameEventQueue, Map<String, ItemEffect> itemEffects) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.itemEffects = itemEffects;
    }

    /**
     * Applies environmental effects to a player based on the current map type.
     *
     * @param player The player to update.
     * @return true if any statistics were changed, false otherwise.
     */
    public boolean applyStats(Player player) {
        final GameMapData map = worldManager.getMap(player.getMapId());
        return switch (map.getType()) {
            case MapType.HAZARD_ZONE -> applyRadiation(player, map.getDifficulty());
            case MapType.SAFE_ZONE -> applyRegeneration(player);
        };
    }

    /**
     * Restores health and reduces radiation for players in safe zones.
     *
     * @param player The player to regenerate.
     * @return true if health or radiation levels were modified.
     */
    private boolean applyRegeneration(Player player) {
        if (player.getHp() < player.getMaxHp()) {
            player.setHp(player.getHp() + 1);
            return true;
        }
        if (player.getRads() > 0) {
            player.setRads(Math.max(0, player.getRads() - 5));
            return true;
        }
        return false;
    }

    /**
     * Increases radiation levels and applies damage if the radiation limit is exceeded.
     *
     * @param player     The player affected by radiation.
     * @param difficulty The difficulty level of the map.
     * @return true if radiation increased or health decreased.
     */
    private boolean applyRadiation(Player player, int difficulty) {
        player.setRads(player.getRads() + (difficulty / 3));

        int maskBonus = 0;
        if (player.getEquippedMaskSlot() != null) {
            final Item mask = player.getInventory().getSlots().get(player.getEquippedMaskSlot());
            if (mask != null) {
                if (mask.getDurability() != null && mask.getDurability() > 0) {
                    mask.setDurability(mask.getDurability() - 1);
                    if (mask.getDurability() <= 0) {
                        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("WARNING: Your mask filter has depleted!", player.getId()));
                    } else if (mask.getHealAmount() != null) {
                        maskBonus = mask.getHealAmount();
                    }
                } else if (mask.getHealAmount() != null && mask.getDurability() == null) {
                    maskBonus = mask.getHealAmount();
                }
            }
        }

        if (player.getRads() > (player.getRadsLimit() + maskBonus)) {
            player.setHp(player.getHp() - difficulty);
        }
        return true;
    }

    /**
     * Processes the usage of a consumable item by delegating to the appropriate effect logic.
     *
     * @param player     The player using the item.
     * @param useRequest The request containing inventory slot information.
     */
    public void useConsumable(Player player, UseRequest useRequest) {
        final Inventory inventory = player.getInventory();
        final Item item = inventory.getSlots().get(useRequest.getSlotIndex());

        if (item == null || item.getType() != ItemType.CONSUMABLE) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Item cannot be used", player.getId()));
            return;
        }

        if (item.getEffect() != null && itemEffects.containsKey(item.getEffect())) {
            final boolean success = itemEffects.get(item.getEffect()).apply(player, item);
            if (success) {
                inventory.removeItem(useRequest.getSlotIndex(), 1);
                gameEventQueue.enqueue(GameEventFactory.sendInventoryEvent(player));
                gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
            }
        }
    }
}
