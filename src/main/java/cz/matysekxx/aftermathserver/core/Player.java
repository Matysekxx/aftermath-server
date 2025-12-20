package cz.matysekxx.aftermathserver.core;

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
    private int x, y;

    private Inventory inventory;

    private int hp = 100;
    private int maxHp = 100;
    private String role;

    private WebSocketSession session;

    public Player(String id, String username, WebSocketSession session) {
        this.id = id;
        this.username = username;
        this.session = session;
        this.inventory = new Inventory(10, 20.0);
    }
}