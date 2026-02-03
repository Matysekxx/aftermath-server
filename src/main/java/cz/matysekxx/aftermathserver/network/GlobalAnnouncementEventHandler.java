package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
        networkService.broadcastGlobalAnnouncement((String) event.payload());
    }
}
