package cz.matysekxx.aftermathserver.core.model.item;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the static definition of an item loaded from configuration.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class ItemTemplate {
    /** Unique identifier for the item type. */
    private String id;
    /** Display name of the item. */
    private String name;
    /** Description shown to the player. */
    private String description;
    /** Maximum items in a single stack. */
    private int maxStack;
    /** Weight of a single unit. */
    private double weight;
    /** The category of the item. */
    private ItemType type;
    /** Amount of health restored on use (if consumable). */
    private Integer healAmount;
    /** Damage dealt (if weapon). */
    private Integer damage;
    /** Attack range in tiles (if weapon). */
    private Integer range;
    /** Cooldown between uses in milliseconds. */
    private Integer cooldown;
    /** Value in game currency. */
    private Integer price;
    /** Identifier for the effect applied on use. */
    private String effect;
}