package cz.matysekxx.aftermathserver.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends Entity {
    private Inventory inventory;
    private String role;
    private int rads = 0;
    private int radsLimit;

    public Player(String id, String username, int x, int y, int maxHp,
                  int inventoryCapacity, double maxWeight, int radsLimit
    ) {
        super(x, y, 0, "default", id, username, maxHp, maxHp, State.ALIVE);
        this.radsLimit = radsLimit;
        this.inventory = new Inventory(inventoryCapacity, maxWeight);
    }

    public void addRads(int amount) {
        rads += amount;
        if (rads > radsLimit) {
            rads = radsLimit;
        }
    }

    public void removeRads(int amount) {
        rads -= amount;
        if (rads < 0) {
            rads = 0;
        }
    }
}