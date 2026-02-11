package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.UseRequest;
import org.springframework.stereotype.Component;

/**
 * Handles using consumable items from inventory.
 * Triggered by the "USE" command.
 *
 * @author Matysekxx
 */
@Component("USE")
public class UseAction extends Action {
    protected UseAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final UseRequest useRequest = objectMapper.convertValue(payload, UseRequest.class);
        gameEngine.processUse(sessionId, useRequest);
    }
}
