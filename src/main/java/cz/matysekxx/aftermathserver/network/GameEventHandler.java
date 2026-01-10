package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;

public abstract class GameEventHandler {
    protected final NetworkService networkService;

    protected GameEventHandler(@Lazy NetworkService networkService) {
        this.networkService = networkService;
    }

    public abstract EventType getType();

    public abstract void handleEvent(GameEvent event);
}
