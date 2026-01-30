package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/// DTO representing an NPC definition loaded from configuration.
@Data
public class NpcTemplate {
    /// Unique identifier for the NPC template (e.g., "mutant_rat").
    private String id;
    /// Display name of the NPC.
    private String name;
    /// Category of the NPC (e.g., "MUTANT", "HUMAN").
    private String type;
    /// The behavior strategy identifier (e.g., "AGGRESSIVE").
    private String behavior;
    /// Whether the NPC initiates combat on sight.
    private boolean aggressive;
    /// Base damage dealt by the NPC.
    private int damage;
    /// Attack range in tiles.
    private int range;
    /// Maximum health points.
    private int maxHp;
    private InteractionType interaction;
    /// List of items that can drop from this NPC.
    private List<Item> loot = new ArrayList<>();
}
