package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class StatsEventHandler extends GameEventHandler {

    public StatsEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_STATS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) networkService.sendStatsToClient(player);
    }
}