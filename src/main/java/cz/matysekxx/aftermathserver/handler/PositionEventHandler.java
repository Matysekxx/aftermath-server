package cz.matysekxx.aftermathserver.handler;

import cz.matysekxx.aftermathserver.core.NetworkService;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class PositionEventHandler extends GameEventHandler {

    public PositionEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_PLAYER_POSITION;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) {
            networkService.sendPosition(player);
        }
    }
}
