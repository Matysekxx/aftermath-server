package cz.matysekxx.aftermathserver.core.model;

/// Categorizes items based on their usage and behavior.
public enum ItemType {
    /// Crafting materials.
    RESOURCE,
    /// Items that can be used (food, meds).
    CONSUMABLE,
    /// Items used for combat.
    WEAPON,
    /// Wearable items.
    EQUIPMENT,
    /// Items with high monetary value.
    VALUABLE,
    /// Items required for quests.
    QUEST
}