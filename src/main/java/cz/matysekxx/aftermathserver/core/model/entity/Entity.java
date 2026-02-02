package cz.matysekxx.aftermathserver.core.model.entity;

import cz.matysekxx.aftermathserver.util.Spatial;
import lombok.Data;

/// Abstract base class for all dynamic entities in the game world.
///
/// Includes Players and NPCs. Handles position, health, and state.
@Data
public abstract class Entity implements Spatial {
    protected int x;
    protected int y;
    protected int layerIndex;
    protected String mapId;
    protected String id;
    protected String name;
    protected int hp;
    protected int maxHp;
    protected State state;

    /// Constructs a new Entity.
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

    /// Reduces health by the specified amount.
    ///
    /// If health drops to 0 or less, state is changed to DEAD.
    public void takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            state = State.DEAD;
        }
    }

    /// Checks if the entity is dead.
    public boolean isDead() {
        return state == State.DEAD;
    }
}
