package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/// Handles `OPEN_METRO_UI` events.
@Component
public class OpenMetroUIEventHandler extends GameEventHandler {

    public OpenMetroUIEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.OPEN_METRO_UI;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Map.Entry<?, ?> entry) {
            @SuppressWarnings("unchecked") final Map.Entry<String, List<MetroStation>> stations = (Map.Entry<String, List<MetroStation>>) entry;
            networkService.sendUIList(stations, event.targetSessionId());
        }
    }
}