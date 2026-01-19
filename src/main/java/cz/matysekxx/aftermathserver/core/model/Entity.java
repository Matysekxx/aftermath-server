package cz.matysekxx.aftermathserver.core.model;

public abstract class Entity {
    public void takeDamage(int amount) {
    }

    public void heal(int amount) {
    }

    public boolean isDead() {
        return false;
    }
}
