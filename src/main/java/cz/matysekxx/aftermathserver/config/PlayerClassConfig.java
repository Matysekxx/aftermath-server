package cz.matysekxx.aftermathserver.config;

import lombok.Data;

/**
 * Configuration for a specific player class.
 *
 * @author Matysekxx
 */
@Data
public class PlayerClassConfig {
    /**
     * Maximum health points for this class.
     */
    private int maxHp;
    /**
     * Maximum number of items in the inventory.
     */
    private int inventoryCapacity;
    /**
     * Maximum weight the player can carry.
     */
    private double maxWeight;
    /**
     * Radiation limit before the player starts taking damage.
     */
    private int radsLimit;
}