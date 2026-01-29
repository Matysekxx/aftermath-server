package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

/// Handles `SEND_NPCS` events by dispatching them to the NetworkService.
@Component
public class SendNpcsEventHandler extends GameEventHandler {
    public SendNpcsEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_NPCS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof List<?> list) {
            @SuppressWarnings("unchecked") final List<NpcDto> npcs = (List<NpcDto>) list;
            networkService.sendNpcs(event.targetSessionId(), event.mapId(), event.isBroadcast(), npcs);
        }
    }
}