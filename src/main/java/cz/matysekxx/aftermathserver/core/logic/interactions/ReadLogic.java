package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("READ")
public class ReadLogic implements InteractionLogic {
    @Override
    public List<GameEvent> interact(MapObject target, Player player) {
        return List.of(GameEvent.create(EventType.SEND_MESSAGE, target.getDescription(), player.getId(), null, false));
    }
}