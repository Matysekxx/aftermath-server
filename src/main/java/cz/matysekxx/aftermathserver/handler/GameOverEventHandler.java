package cz.matysekxx.aftermathserver.handler;

import cz.matysekxx.aftermathserver.core.NetworkService;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

@Component
public class GameOverEventHandler extends GameEventHandler {

    public GameOverEventHandler(NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_GAME_OVER;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) networkService.sendGameOver(player);
    }
}