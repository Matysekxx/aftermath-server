package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.web.socket.WebSocketSession;


public abstract class Action {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected final String type;
    protected final GameEngine gameEngine;

    protected Action(String type, GameEngine gameEngine) {
        this.type = type;
        this.gameEngine = gameEngine;
    }

    public abstract void execute(WebSocketSession session, JsonNode payload);
}
