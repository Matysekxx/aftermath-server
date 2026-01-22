package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

/// Handles `SEND_MESSAGE` events by sending a generic text message to the client or broadcasting it.
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
        if (event.payload() instanceof TextMessage message) {
            if (event.isBroadcast()) networkService.broadcastToMap(message.getPayload(), event.mapId());
            else networkService.sendToClient(message.getPayload(), event.targetSessionId());
        }
    }
}
