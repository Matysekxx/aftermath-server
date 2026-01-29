package cz.matysekxx.aftermathserver.core.model.item;

import lombok.*;

/// Represents an item in the game.
///
/// Items can be stored in inventories, dropped on the map, or used.
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Item {
    /// Unique identifier for the item type.
    private String id;
    /// Display name of the item.
    private String name;
    /// Description shown to the player.
    private String description;

    /// Current quantity in stack.
    private int quantity;
    /// Maximum items in a single stack.
    private int maxStack;
    /// Weight of a single unit.
    private double weight;

    /// The category of the item.
    private ItemType type;
    /// Amount of health restored on use (if consumable).
    private Integer healAmount;
    /// Damage dealt (if weapon).
    private Integer damage;
    private Integer range;

    /// Value in game currency.
    private Integer price;
    private String effect;

    /// Calculates the total weight of the item stack.
    public final double getTotalWeight() {
        return weight * quantity;
    }

    /// Creates a copy of this item with a specific quantity.
    public Item cloneWithQuantity(int quantity) {
        return this.toBuilder().quantity(quantity).build();
    }
}