package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/// Handles dropping items from inventory.
///
/// Triggered by the `DROP` command.
/// Removes an item from the player's inventory and places it on the map at the player's location.
@Component("DROP")
public class DropAction extends Action {
    public DropAction(GameEngine gameEngine) {
        super("DROP", gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final int slotIndex = payload.get("slotIndex").asInt();
        final int amount = payload.has("amount") ? payload.get("amount").asInt() : 1;
        gameEngine.dropItem(sessionId, slotIndex, amount);
    }
}