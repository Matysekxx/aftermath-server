package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageEventHandler extends GameEventHandler{
    protected ChatMessageEventHandler(@Lazy NetworkService networkService) {
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
