package cz.matysekxx.aftermathserver.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Item {
    private String id;
    private String name;
    private String description;
    private String symbol;

    private int quantity;
    private int maxStack;
    private double weight;

    public double getTotalWeight() {
        return weight * quantity;
    }

    public Item cloneWithQuantity(int quantity) {
        return new Item(
                this.id,
                this.name,
                this.description,
                this.symbol,
                quantity,
                this.maxStack,
                this.weight
        );
    }
}