package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;

public interface InteractionLogic {
    WebSocketResponse interact(MapObject target, Player player);
}
