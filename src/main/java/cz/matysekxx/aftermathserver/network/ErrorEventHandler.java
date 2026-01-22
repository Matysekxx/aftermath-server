package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/// Handles `SEND_ERROR` events by sending an error message to the client.
@Component
public class ErrorEventHandler extends GameEventHandler {
    public ErrorEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_ERROR;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof String message) {
            networkService.sendError(event.targetSessionId(), message);
        }
    }
}
