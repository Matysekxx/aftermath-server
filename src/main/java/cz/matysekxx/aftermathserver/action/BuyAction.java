package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.BuyRequest;
import org.springframework.stereotype.Component;

@Component("BUY")
public class BuyAction extends Action {

    public BuyAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final BuyRequest request = objectMapper.convertValue(payload, BuyRequest.class);
        gameEngine.processBuy(sessionId, request);
    }
}