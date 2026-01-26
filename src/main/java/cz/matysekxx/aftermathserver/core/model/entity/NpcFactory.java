package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.behavior.*;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/// Factory for creating NPC instances from templates.
@Service
public class NpcFactory {
    private final NpcTable npcTable;
    private final ItemFactory itemFactory;

    public NpcFactory(NpcTable npcTable, ItemFactory itemFactory) {
        this.npcTable = npcTable;
        this.itemFactory = itemFactory;
    }

    /// Creates a new NPC instance based on a template ID.
    ///
    /// @param id The template ID defined in configuration (e.g., "mutant_rat").
    /// @param x The X coordinate on the map.
    /// @param y The Y coordinate on the map.
    /// @param layerIndex The layer index (Z coordinate).
    /// @param mapId The ID of the map where the NPC is spawned.
    /// @return A fully initialized Npc entity with behavior and loot.
    public Npc createNpc(String id, int x,  int y, int layerIndex, String mapId) {
        final NpcTemplate template = npcTable.getTemplate(id);
        if (template == null) {
            throw new IllegalArgumentException("Unknown NPC template: " + id);
        }
        final Behavior behavior = switch (template.getBehavior().toUpperCase()) {
            case "AGGRESSIVE" -> new AggressiveBehavior();
            case "PATROL" -> new PatrolBehavior();
            case "STATIONARY" -> new StationaryBehavior();
            case "IDLE" -> new IdleBehavior();
            default -> throw new IllegalStateException("Unexpected value: " + template.getBehavior());
        };
        final String instanceId = template.getId() + "_" + UUID.randomUUID().toString().substring(0,8);
        final Npc npc = new Npc(
                instanceId,
                template.getName(),
                x, y,
                layerIndex,
                mapId,
                template.getMaxHp(),
                behavior
        );
        npc.setType(template.getType());
        npc.setAggressive(template.isAggressive());
        npc.setDamage(template.getDamage());
        npc.setRange(template.getRange());

        final Collection<Item> loot = new ArrayList<>();
        if (template.getLoot() != null) {
            for (Item item : template.getLoot()) {
                loot.add(itemFactory.createItem(item.getId(), item.getQuantity()));
            }
        }
        npc.setLoot(loot);
        return npc;
    }
}
