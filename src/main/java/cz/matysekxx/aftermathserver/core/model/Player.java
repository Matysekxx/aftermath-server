package cz.matysekxx.aftermathserver.core.model;

import lombok.*;

@Getter
@Setter
@Builder
public class Player {
    private String id;
    private String username;
    private String currentMapId;
    private int x, y;
    private int currentLayer;
    private Inventory inventory;

    private int hp = 100;
    private int maxHp = 100;
    private String role;

    private int rads = 0;
    private int radsLimit = 20;

    private State state;

    public Player(String id, String username) {
        this.id = id;
        this.username = username;
        this.inventory = new Inventory(10, 20.0);
        this.currentLayer = 0;
    }

    public enum State{
        ALIVE, DEAD, POISONED
    }
}