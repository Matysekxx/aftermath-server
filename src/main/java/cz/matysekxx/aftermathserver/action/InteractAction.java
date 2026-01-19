package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.InteractRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component("INTERACT")
public class InteractAction extends Action {

    public InteractAction(GameEngine gameEngine) {
        super("INTERACT_RESULT", gameEngine);
    }

    @Override
    public void execute(WebSocketSession session, JsonNode payload) {
        final var request = objectMapper.convertValue(payload, InteractRequest.class);
        gameEngine.processInteract(session.getId(), request.getTarget());
    }
}
