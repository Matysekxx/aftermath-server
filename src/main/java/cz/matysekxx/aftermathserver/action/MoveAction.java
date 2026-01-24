package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import org.springframework.stereotype.Component;

/// Handles player movement.
///
/// Triggered by the `MOVE` command.
/// Updates the player's position based on the requested direction (UP, DOWN, LEFT, RIGHT).
@Component("MOVE")
public class MoveAction extends Action {

    public MoveAction(GameEngine gameEngine) {
        super("PLAYER_MOVED", gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final var request = objectMapper.convertValue(payload, MoveRequest.class);
        gameEngine.processMove(sessionId, request);
    }
}
