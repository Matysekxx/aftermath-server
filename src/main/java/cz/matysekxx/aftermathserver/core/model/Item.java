package cz.matysekxx.aftermathserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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