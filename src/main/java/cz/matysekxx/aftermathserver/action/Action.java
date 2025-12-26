package cz.matysekxx.aftermathserver.action;

import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


public abstract class Action {
    protected final String type;
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected Action(String type) { this.type = type; }
    public abstract WebSocketResponse execute(WebSocketSession session, JsonNode payload);
}
