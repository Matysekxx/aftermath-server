package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.SellRequest;
import org.springframework.stereotype.Component;

/**
 * Handles selling items to NPC traders.
 * Triggered by the "SELL" command.
 *
 * @author Matysekxx
 */
@Component("SELL")
public class SellAction extends Action {
    public SellAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final SellRequest request = objectMapper.convertValue(payload, SellRequest.class);
        gameEngine.processSell(sessionId, request);

    }
}
