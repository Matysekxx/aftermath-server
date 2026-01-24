package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.behavior.Behavior;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/// Represents a Non-Player Character in the game world.
///
/// NPCs can be friendly traders, quest givers, or hostile mutants.
/// Their behavior is governed by the assigned [Behavior] strategy.
@Getter
@Setter
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

    /// ID of the dialogue tree associated with this NPC (if interactive).
    private String dialogueId;

    public Npc(String id, String name, int x, int y, int layerIndex, String mapId, int maxHp, Behavior behavior) {
        super(x, y, layerIndex, mapId, id, name, maxHp, maxHp, State.ALIVE);
        this.behavior = behavior;
    }
}
