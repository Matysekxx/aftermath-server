package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.util.Vector3;
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
    private Integer equippedWeaponSlot;
    private Integer equippedMaskSlot;

    /// Creates a new Player instance.
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