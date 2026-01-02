package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.InteractRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component("INTERACT")
public class InteractAction extends Action {
    private final GameEngine gameEngine;
    public InteractAction(GameEngine gameEngine) {
        super("INTERACT_RESULT");
        this.gameEngine = gameEngine;
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final var request = objectMapper.convertValue(payload, InteractRequest.class);
        return gameEngine.processInteract(session.getId(), request.getTarget());
    }
}
