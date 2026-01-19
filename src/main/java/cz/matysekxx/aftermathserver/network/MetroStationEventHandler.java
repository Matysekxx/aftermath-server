package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;

public class MetroStationEventHandler extends GameEventHandler {
    protected MetroStationEventHandler(NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return null;
    }

    @Override
    public void handleEvent(GameEvent event) {
        //TODO: implementovat logiku pro cestovani
    }
}
