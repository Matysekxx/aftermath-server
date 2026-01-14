package cz.matysekxx.aftermathserver.core.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Item {
    private String id;
    private String name;
    private String description;

    private int quantity;
    private int maxStack;
    private double weight;

    private ItemType type;
    private Integer healAmount;
    private Integer damage;
    private Integer price;

    public final double getTotalWeight() {
        return weight * quantity;
    }

    public Item cloneWithQuantity(int quantity) {
        return this.toBuilder().quantity(quantity).build();
    }

    public void use(Player player) {
        //TODO: implementovat logiku pouziti itemu
    }
}