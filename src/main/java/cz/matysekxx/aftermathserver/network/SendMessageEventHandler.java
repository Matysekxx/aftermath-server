package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_MESSAGE} events by sending a generic text message to the client or broadcasting it.
 *
 * @author Matysekxx
 */
@Component
public class SendMessageEventHandler extends GameEventHandler {

    protected SendMessageEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_MESSAGE;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof String message) {
            if (event.isBroadcast()) networkService.broadcastToMap(message, event.mapId());
            else networkService.sendToClient(message, event.targetSessionId());
        }
    }
}
