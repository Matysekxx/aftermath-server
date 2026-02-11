package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_GAME_OVER} events by notifying the client that the game has ended.
 *
 * @author Matysekxx
 */
@Component
public class GameOverEventHandler extends GameEventHandler {

    public GameOverEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_GAME_OVER;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) networkService.sendGameOver(player.getId());
    }
}