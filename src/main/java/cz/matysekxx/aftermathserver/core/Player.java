package cz.matysekxx.aftermathserver.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String id;
    private String username;
    private int x,y;
    private WebSocketSession session;
}
