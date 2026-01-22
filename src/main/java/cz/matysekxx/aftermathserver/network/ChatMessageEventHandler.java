package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/// Handles `BROADCAST_CHAT_MSG` events by broadcasting a chat message to all players on the map.
@Component
public class ChatMessageEventHandler extends GameEventHandler {
    public ChatMessageEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.BROADCAST_CHAT_MSG;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof ChatRequest chatRequest) {
            networkService.broadcastToMap(chatRequest.getMessage(), event.mapId());
        }
    }
}
