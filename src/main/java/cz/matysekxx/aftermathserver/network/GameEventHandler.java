package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;

/// Abstract base class for handling specific game events.
///
/// Implementations are responsible for processing events of a specific `EventType`.
public abstract class GameEventHandler {
    protected final NetworkService networkService;

    protected GameEventHandler(@Lazy NetworkService networkService) {
        this.networkService = networkService;
    }

    /// Returns the type of event this handler processes.
    public abstract EventType getType();

    /// Processes the given event.
    public abstract void handleEvent(GameEvent event);
}
