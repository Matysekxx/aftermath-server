package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.behavior.Behavior;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Represents a Non-Player Character in the game world.
///
/// NPCs can be friendly traders, quest givers, or hostile mutants.
/// Their behavior is governed by the assigned [Behavior] strategy.

@EqualsAndHashCode(callSuper = true)
@Data
public class Npc extends Entity {
    /// The AI behavior strategy assigned to this NPC.
    private Behavior behavior;

    /// The type of the NPC (e.g., "MUTANT", "TRADER", "SOLDIER").
    private String type;

    /// Indicates if the NPC is hostile towards players.
    private boolean aggressive;

    /// Base damage dealt by this NPC in combat.
    private int damage;

    /// Range of vision or attack.
    private int range;

    /// Items dropped when this NPC dies.
    private Collection<Item> loot = new ArrayList<>();
    /// Items available for purchase from the npc.
    private List<Item> shopItems = new ArrayList<>();

    /// ID of the dialogue tree associated with this NPC (if interactive).
    private String dialogueId;
    private InteractionType interaction;

    /// Constructs a new Npc instance.
    public Npc(String id, String name, int x, int y, int layerIndex, String mapId, int maxHp, Behavior behavior, InteractionType interaction) {
        super(x, y, layerIndex, mapId, id, name, maxHp, maxHp, State.ALIVE);
        this.behavior = behavior;
        this.interaction = interaction;
    }

    /// Updates the NPC state by executing its behavior logic.
    public void update(GameMapData gameMapData, Collection<Player> players) {
        behavior.update(this, gameMapData, players);
    }

    /// Creates an NPC instance from a template.
    public static Npc fromTemplate(String instanceId, NpcTemplate template, int x, int y, int layerIndex, String mapId, Behavior behavior) {
        final Npc npc = new Npc(instanceId, template.getName(), x, y, layerIndex, mapId, template.getMaxHp(), behavior, template.getInteraction());
        npc.setType(template.getType());
        npc.setAggressive(template.isAggressive());
        npc.setDamage(template.getDamage());
        npc.setRange(template.getRange());
        npc.setDialogueId(template.getDialogId());
        npc.setShopItems(new ArrayList<>(template.getShopItems()));
        return npc;
    }
}
