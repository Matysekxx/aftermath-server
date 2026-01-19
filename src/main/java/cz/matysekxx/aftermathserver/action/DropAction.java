package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component("DROP")
public class DropAction extends Action {
    public DropAction(GameEngine gameEngine) {
        super("DROP", gameEngine);
    }

    @Override
    public void execute(WebSocketSession session, JsonNode payload) {
        final int slotIndex = payload.get("slotIndex").asInt();
        final int amount = payload.has("amount") ? payload.get("amount").asInt() : 1;
        gameEngine.dropItem(session.getId(), slotIndex, amount);
    }
}