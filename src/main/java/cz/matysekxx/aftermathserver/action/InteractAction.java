package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.InteractRequest;
import org.springframework.stereotype.Component;

/// Handles interactions with map objects.
///
/// Triggered by the `INTERACT` command.
/// Used for looting containers, talking to NPCs, or using world objects.
@Component("INTERACT")
public class InteractAction extends Action {

    public InteractAction(GameEngine gameEngine) {
        super("INTERACT_RESULT", gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final var request = objectMapper.convertValue(payload, InteractRequest.class);
        gameEngine.processInteract(sessionId, request.getTarget());
    }
}
