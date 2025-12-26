package cz.matysekxx.aftermathserver.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
@NoArgsConstructor
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

    private boolean isDead = false;

    private WebSocketSession session;

    public Player(String id, String username, WebSocketSession session) {
        this.id = id;
        this.username = username;
        this.session = session;
        this.inventory = new Inventory(10, 20.0);
        this.currentLayer = 0;
    }
}