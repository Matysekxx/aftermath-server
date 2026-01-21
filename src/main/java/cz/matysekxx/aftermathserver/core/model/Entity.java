package cz.matysekxx.aftermathserver.core.model;

import lombok.Data;

@Data
public abstract class Entity {
    protected int x;
    protected int y;
    protected int layerIndex;
    protected String mapId;
    protected String id;
    protected String username;
    protected int hp;
    protected int maxHp;
    protected State state;

    public Entity(int x, int y, int layerIndex, String mapId,
                  String id, String username, int hp, int maxHp,
                  State state
    ) {
        this.x = x;
        this.y = y;
        this.layerIndex = layerIndex;
        this.mapId = mapId;
        this.id = id;
        this.username = username;
        this.hp = hp;
        this.maxHp = maxHp;
        this.state = state;
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            state = State.DEAD;
        }
    }

    public void heal(int amount) {
        hp += amount;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public boolean isDead() {
        return state == State.DEAD;
    }
}
