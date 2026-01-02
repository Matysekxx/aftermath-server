package cz.matysekxx.aftermathserver.core.logic;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.MapLoadPayload;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;

@Component("TRAVEL")
public class TravelLogic implements InteractionLogic {
    private final WorldManager worldManager;

    public TravelLogic(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public WebSocketResponse interact(MapObject target, Player player) {
        final String nextMapId = target.getTargetMapId();
        final GameMapData nextMap = worldManager.getMap(nextMapId);

        if (nextMap == null) {
            return WebSocketResponse.of("ACTION_FAILED", "Target map does not exist (server error).");
        }

        player.setMapId(nextMapId);
        player.setLayerIndex(0); // placeholder
        player.setX(target.getTargetX());
        player.setY(target.getTargetY());

        final MapLoadPayload mapPayload = new MapLoadPayload(
                nextMap.getId(),
                nextMap.getName(),
                nextMap.getLayout(),
                nextMap.getEnvironment(),
                nextMap.getParsedLayers()
        );
        return WebSocketResponse.of("MAP_LOAD", mapPayload);
    }
}