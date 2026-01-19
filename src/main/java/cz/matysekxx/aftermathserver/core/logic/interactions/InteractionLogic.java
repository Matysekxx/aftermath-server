package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import cz.matysekxx.aftermathserver.event.GameEvent;

import java.util.List;

public interface InteractionLogic {
    List<GameEvent> interact(MapObject target, Player player);
}
