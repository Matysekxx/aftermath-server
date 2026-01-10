package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;


public abstract class Action {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected final String type;

    protected Action(String type) {
        this.type = type;
    }

    public abstract WebSocketResponse execute(WebSocketSession session, JsonNode payload);
}
