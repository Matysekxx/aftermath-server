package cz.matysekxx.aftermathserver.core.factory;

import cz.matysekxx.aftermathserver.core.model.behavior.*;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTable;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTemplate;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory for creating NPC instances from templates.
 *
 * @author Matysekxx
 */
@Service
public class NpcFactory {
    private final NpcTable npcTable;
    private final ItemFactory itemFactory;
    private final GameEventQueue gameEventQueue;

    public NpcFactory(NpcTable npcTable, ItemFactory itemFactory, GameEventQueue gameEventQueue) {
        this.npcTable = npcTable;
        this.itemFactory = itemFactory;
        this.gameEventQueue = gameEventQueue;
    }

    /**
     * Creates a new NPC instance based on a template ID.
     *
     * @param id         The template ID defined in configuration (e.g., "mutant_rat").
     * @param x          The X coordinate on the map.
     * @param y          The Y coordinate on the map.
     * @param layerIndex The layer index (Z coordinate).
     * @param mapId      The ID of the map where the NPC is spawned.
     * @return A fully initialized Npc entity with behavior and loot.
     */
    public Npc createNpc(String id, int x, int y, int layerIndex, String mapId) {
        final NpcTemplate template = npcTable.getTemplate(id);
        final Behavior behavior = getBehavior(id, template);

        final String instanceId = template.getId() + "_" + UUID.randomUUID().toString().substring(0, 8);
        final Npc npc = Npc.fromTemplate(instanceId, template, x, y, layerIndex, mapId, behavior);

        final List<Item> shopItems = new ArrayList<>();
        if (template.getShopItems() != null) {
            for (Item item : template.getShopItems()) {
                try {
                    shopItems.add(itemFactory.createItem(item.getId(), item.getQuantity()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error creating shop item for NPC " + id + ": " + e.getMessage());
                }
            }
        }
        npc.setShopItems(shopItems);
        
        npc.setType(template.getType());
        npc.setAggressive(template.isAggressive());
        npc.setDamage(template.getDamage());
        npc.setRange(template.getRange());

        final Collection<Item> loot = new ArrayList<>();
        if (template.getLoot() != null) {
            for (Item item : template.getLoot()) {
                try {
                    final Item createdItem = itemFactory.createItem(item.getId(), item.getQuantity());
                    if (shouldSpawnItem(createdItem)) {
                        loot.add(createdItem);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Error creating loot item for NPC " + id + ": " + e.getMessage());
                }
            }
        }
        npc.setLoot(loot);
        return npc;
    }

    private @NonNull Behavior getBehavior(String id, NpcTemplate template) {
        if (template == null) {
            throw new IllegalArgumentException("Unknown NPC template: " + id);
        }

        final String behaviorType = template.getBehavior() != null ? template.getBehavior().toUpperCase() : "STATIONARY";
        return switch (behaviorType) {
            case "AGGRESSIVE" -> new AggressiveBehavior(gameEventQueue);
            case "IDLE" -> new IdleBehavior();
            case "STATIONARY" -> new StationaryBehavior();
            default -> new StationaryBehavior();
        };
    }

    private boolean shouldSpawnItem(Item item) {
        if (item.getRarity() == null) return true;
        final double chance = switch (item.getRarity().toUpperCase()) {
            case "COMMON" -> 1.0;
            case "UNCOMMON" -> 0.5;
            case "RARE" -> 0.25;
            case "EPIC" -> 0.1;
            case "LEGENDARY" -> 0.01;
            default -> 1.0;
        };
        return ThreadLocalRandom.current().nextDouble() < chance;
    }
}