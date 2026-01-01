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

    private int hp = 100;
    private int maxHp = 100;
    private String role;

    private int rads = 0;
    private int radsLimit = 20;

    private State state;

    public Player(String id, String username, int x, int y) {
        this.id = id;
        this.username = username;
        this.inventory = new Inventory(10, 20.0);
        this.state = State.ALIVE;
    }

    public enum State{
        ALIVE, DEAD, POISONED
    }
}