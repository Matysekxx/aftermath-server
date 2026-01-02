package cz.matysekxx.aftermathserver.core.logic;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;

@Component("READ")
public class ReadLogic implements InteractionLogic {
    @Override
    public WebSocketResponse interact(MapObject target, Player player) {
        return WebSocketResponse.of("NOTIFICATION", target.getDescription());
    }
}