package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code GLOBAL_ANNOUNCEMENT} events by broadcasting a message to all connected players.
 *
 * @author Matysekxx
 */
@Component
public class GlobalAnnouncementEventHandler extends GameEventHandler {

    public GlobalAnnouncementEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.GLOBAL_ANNOUNCEMENT;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof String message) {
            networkService.broadcastGlobalAnnouncement(message);
        }
    }
}
