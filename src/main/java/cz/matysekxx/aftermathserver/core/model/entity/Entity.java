package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.util.Spatial;
import lombok.Data;

/**
 * Abstract base class for all dynamic entities in the game world.
 * <p>
 * Provides common functionality for position tracking, health management,
 * and state transitions for both Players and NPCs.
 *
 * @author Matysekxx
 */
@Data
public abstract class Entity implements Spatial {
    /** The X coordinate of the entity. */
    protected int x;
    /** The Y coordinate of the entity. */
    protected int y;
    /** The layer index (Z coordinate) of the entity. */
    protected int layerIndex;
    /** The ID of the map the entity is currently on. */
    protected String mapId;
    /** Unique identifier for the entity. */
    protected String id;
    /** Display name of the entity. */
    protected String name;
    /** Current health points of the entity. */
    protected int hp;
    /** Maximum health points of the entity. */
    protected int maxHp;
    /** Current state of the entity. */
    protected State state;

    /**
     * Constructs a new Entity.
     *
     * @param x          The X coordinate.
     * @param y          The Y coordinate.
     * @param layerIndex The layer index.
     * @param mapId      The map ID.
     * @param id         The entity ID.
     * @param name       The entity name.
     * @param hp         The current health points.
     * @param maxHp      The maximum health points.
     * @param state      The initial state.
     */
    public Entity(int x, int y, int layerIndex, String mapId,
                  String id, String name, int hp, int maxHp,
                  State state
    ) {
        this.x = x;
        this.y = y;
        this.layerIndex = layerIndex;
        this.mapId = mapId;
        this.id = id;
        this.name = name;
        this.hp = hp;
        this.maxHp = maxHp;
        this.state = state;
    }

    /**
     * Reduces health by the specified amount.
     * <p>
     * If health drops to 0 or less, state is changed to DEAD.
     */
    public void takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            state = State.DEAD;
        }
    }

    /**
     * Checks if the entity is dead.
     *
     * @return {@code true} if the entity's state is DEAD, {@code false} otherwise.
     */
    public boolean isDead() {
        return state == State.DEAD;
    }
}
