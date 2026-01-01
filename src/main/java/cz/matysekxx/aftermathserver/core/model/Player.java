package cz.matysekxx.aftermathserver.core.model;

import cz.matysekxx.aftermathserver.core.world.GameLocation;
import lombok.*;

import java.awt.*;

@Getter
@Setter
public class Player {
    private String id;
    private String username;
    private GameLocation location;
    private Inventory inventory;

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
        this.location = new GameLocation(null, 0, new Point(x,y));
        this.state = State.ALIVE;
    }

    public enum State{
        ALIVE, DEAD, POISONED
    }

    public void teleport(GameLocation destination) {
        this.location = destination;
    }

    public int getX() {
        return location.getPosition().x;
    }

    public int getY() {
        return location.getPosition().y;
    }

    public void setX(int x) {
        this.location.getPosition().x = x;
    }

    public void setY(int y) {
        this.location.getPosition().y = y;
    }
}