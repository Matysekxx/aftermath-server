package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;

/**
 * Abstract base class for handling specific game events.
 * <p>
 * Implementations are responsible for processing events of a specific {@link EventType}.
 *
 * @author Matysekxx
 */
public abstract class GameEventHandler {
    protected final NetworkService networkService;

    protected GameEventHandler(@Lazy NetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * @return The type of event this handler processes.
     */
    public abstract EventType getType();

    /**
     * Processes the given event.
     *
     * @param event The event to handle.
     */
    public abstract void handleEvent(GameEvent event);
}
