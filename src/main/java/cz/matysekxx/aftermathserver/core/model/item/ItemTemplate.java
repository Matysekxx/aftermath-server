package cz.matysekxx.aftermathserver.core.model.item;

import lombok.Data;
import lombok.NoArgsConstructor;

/// Represents the static definition of an item loaded from configuration.
@Data
@NoArgsConstructor
public class ItemTemplate {
    private String id;
    private String name;
    private String description;
    private int maxStack;
    private double weight;
    private ItemType type;
    private Integer healAmount;
    private Integer damage;
    private Integer range;
    private Integer cooldown;
    private Integer price;
    private String effect;
}