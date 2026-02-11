package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.MapViewportPayload;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_MAP_DATA} events by sending map layout and metadata to the client.
 *
 * @author Matysekxx
 */
@Component
public class MapDataEventHandler extends GameEventHandler {

    public MapDataEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_MAP_DATA;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof MapViewportPayload viewport) {
            networkService.sendMapData(event.targetSessionId(), viewport);
        }
    }
}