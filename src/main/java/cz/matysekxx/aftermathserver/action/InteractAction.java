package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;

public class InteractAction extends Action {
    private final GameEngine gameEngine;
    public InteractAction(GameEngine gameEngine) {
        super("INTERACT_RESULT");
        this.gameEngine = gameEngine;
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final GameDtos.InteractReq request = objectMapper.convertValue(payload, GameDtos.InteractReq.class);
        return gameEngine.processInteract(session.getId(), request.getItemId());
    }
}
