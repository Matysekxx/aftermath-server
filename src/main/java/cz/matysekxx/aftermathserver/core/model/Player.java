package cz.matysekxx.aftermathserver.core.model;

import lombok.Getter;
import lombok.Setter;

/// Represents a player in the game.
///
/// Extends Entity with player-specific attributes like inventory, role, and radiation.
@Getter
@Setter
public class Player extends Entity {
    private Inventory inventory;
    private String role;
    private int rads = 0;
    private int radsLimit;
    private int credits;
    private int debt;
    private int pendingCosts;

    /// Creates a new Player instance.
    public Player(String id, String username, int x, int y, int maxHp,
                  int inventoryCapacity, double maxWeight, int radsLimit
    ) {
        super(x, y, 0, "default", id, username, maxHp, maxHp, State.ALIVE);
        this.radsLimit = radsLimit;
        this.inventory = new Inventory(inventoryCapacity, maxWeight);
    }

    /// Increases radiation level, capped at the limit.
    public void addRads(int amount) {
        rads += amount;
        if (rads > radsLimit) {
            rads = radsLimit;
        }
    }

    /// Reduces radiation level, floored at 0.
    public void removeRads(int amount) {
        rads -= amount;
        if (rads < 0) {
            rads = 0;
        }
    }

    public void addCredits(int amount) {
        credits += amount;
    }

    public void removeCredits(int amount) {
        credits -= amount;

    }
}