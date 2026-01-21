package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapObjectEventHandler extends GameEventHandler {

    public MapObjectEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_MAP_OBJECTS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof List<?> list) {
            @SuppressWarnings("unchecked") final List<MapObject> mapObjects = (List<MapObject>) list;
            if (event.isBroadcast()) {
                networkService.broadcastMapObjects(mapObjects, event.mapId());
            } else {
                networkService.sendMapObjects(event.targetSessionId(), mapObjects);
            }
        }
    }
}