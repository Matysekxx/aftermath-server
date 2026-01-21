package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.util.Tuple;
import org.springframework.context.annotation.Lazy;

import java.util.List;

public class OpenMetroUIEventHandler extends GameEventHandler {

    protected OpenMetroUIEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.OPEN_METRO_UI;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Tuple<?,?> tuple) {
            @SuppressWarnings("unchecked")
            Tuple<String, List<MetroStation>> payload = (Tuple<String, List<MetroStation>>) tuple;
            networkService.sendUIList(payload, event.targetSessionId());
        }
    }
}
