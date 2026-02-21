package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a player in the game world.
 * <p>
 * Extends {@link Entity} with player-specific attributes such as inventory management,
 * character roles, radiation levels, and economic status (credits/debt).
 *
 * @author Matysekxx
 */
@Getter
@Setter
public class Player extends Entity {
    /**
     * The player's inventory.
     */
    private Inventory inventory;
    /**
     * The player's role or class name.
     */
    private String role;
    /**
     * Current radiation level of the player.
     */
    private int rads = 0;
    /**
     * Radiation limit before the player starts taking damage.
     */
    private int radsLimit;
    /**
     * Current credits balance of the player.
     */
    private int credits;
    /**
     * Current debt of the player.
     */
    private int debt;
    /**
     * Costs accumulated during the day to be added to debt.
     */
    private int pendingCosts;
    /**
     * Inventory slot index of the equipped weapon.
     */
    private Integer equippedWeaponSlot;
    /**
     * Inventory slot index of the equipped mask.
     */
    private Integer equippedMaskSlot;
    /**
     * Timestamp of the last attack performed by the player.
     */
    private long lastAttackTime;

    /**
     * Creates a new Player instance.
     *
     * @param id                The session ID.
     * @param username          The player's username.
     * @param spawn             The spawn coordinate.
     * @param playerClassConfig The configuration for the player's class.
     * @param mapId             The ID of the map the player is on.
     * @param role              The player's role/class name.
     */
    public Player(String id, String username, Vector3 spawn,
                  PlayerClassConfig playerClassConfig, String mapId,
                  String role
    ) {
        super(spawn.x(), spawn.y(), spawn.z(), mapId, id, username,
                playerClassConfig.getMaxHp(), playerClassConfig.getMaxHp(),
                State.ALIVE
        );
        this.radsLimit = playerClassConfig.getRadsLimit();
        this.inventory = new Inventory(playerClassConfig.getInventoryCapacity(),
                playerClassConfig.getMaxWeight()
        );
        this.role = role;
    }

    /**
     * Increases radiation level, capped at the limit.
     *
     * @param amount The amount of radiation to add.
     */
    public void addRads(int amount) {
        rads += amount;
        if (rads > radsLimit) {
            rads = radsLimit;
        }
    }

    /**
     * Reduces radiation level, floored at 0.
     *
     * @param amount The amount of radiation to remove.
     */
    public void removeRads(int amount) {
        rads -= amount;
        if (rads < 0) {
            rads = 0;
        }
    }

    /**
     * Adds credits to the player's balance.
     *
     * @param amount The amount of credits to add.
     */
    public void addCredits(int amount) {
        credits += amount;
    }

    /**
     * Removes credits from the player's balance.
     *
     * @param amount The amount of credits to remove.
     */
    public void removeCredits(int amount) {
        credits -= amount;

    }
}