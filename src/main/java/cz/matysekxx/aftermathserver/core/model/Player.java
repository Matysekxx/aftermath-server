package cz.matysekxx.aftermathserver.core.model;

import lombok.*;

@Getter
@Setter
public class Player {
    private String id;
    private String username;
    private Inventory inventory;
    private String mapId;
    private int layerIndex;
    private int x,y;

    private int hp;
    private int maxHp;
    private String role;

    private int rads = 0;
    private int radsLimit;

    private State state;

    public Player(String id, String username, int x, int y, int maxHp, int inventoryCapacity, double maxWeight, int radsLimit) {
        this.id = id;
        this.username = username;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.radsLimit = radsLimit;
        this.inventory = new Inventory(inventoryCapacity, maxWeight);
        this.state = State.ALIVE;
        this.x = x;
        this.y = y;

    }

    public enum State{
        ALIVE, DEAD, TRAVELLING, POISONED
    }
}