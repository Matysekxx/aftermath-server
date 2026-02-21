package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.PayDebtRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import org.springframework.stereotype.Component;

/**
 * Action handling the debt payment request from the client.
 *
 * @author Matysekxx
 */
@Component("PAY_DEBT")
public class PayDebtAction extends Action {
    private final GameEventQueue gameEventQueue;

    public PayDebtAction(GameEventQueue gameEventQueue, GameEngine gameEngine) {
        super(gameEngine);
        this.gameEventQueue = gameEventQueue;
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final PayDebtRequest request = objectMapper.convertValue(payload, PayDebtRequest.class);
        gameEventQueue.enqueue(GameEvent.create(EventType.PAY_DEBT, request, sessionId, null, false));
    }
}