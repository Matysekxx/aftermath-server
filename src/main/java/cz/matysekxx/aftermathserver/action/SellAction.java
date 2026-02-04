package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.SellRequest;
import org.springframework.stereotype.Component;

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
