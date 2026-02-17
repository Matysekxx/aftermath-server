package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.GlobalState;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_STATS} events by sending updated player statistics to the client.
 *
 * @author Matysekxx
 */
@Component
public class StatsEventHandler extends GameEventHandler {

    private final GlobalState globalState;

    public StatsEventHandler(@Lazy NetworkService networkService, GlobalState globalState) {
        super(networkService);
        this.globalState = globalState;
    }

    @Override
    public EventType getType() {
        return EventType.SEND_STATS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) {
            networkService.sendStatsToClient(player, globalState.getGlobalDebt());
        }
    }
}