package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;

public class DropAction extends Action {

    private final GameEngine gameEngine;

    public DropAction(GameEngine gameEngine) {
        super("DROP");
        this.gameEngine = gameEngine;
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final int slotIndex = payload.get("slotIndex").asInt();
        final int amount = payload.has("amount") ? payload.get("amount").asInt() : 1;
        return gameEngine.dropItem(session.getId(), slotIndex, amount);
    }
}