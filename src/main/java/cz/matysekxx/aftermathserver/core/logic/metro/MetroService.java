package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.State;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MetroService {
    private final Map<String, List<MetroStation>> metroStations;
    private final GameEventQueue gameEventQueue;
    private final WorldManager worldManager;

    @Autowired
    public MetroService(@Qualifier("metroMapData") Map<String, List<MetroStation>> metroStations, GameEventQueue gameEventQueue, WorldManager worldManager) {
        this.metroStations = metroStations;
        this.gameEventQueue = gameEventQueue;
        this.worldManager = worldManager;
        log.info("MetroService initialized");
    }

    public void handleStationTrigger(Player player, String lineId) {
        final List<MetroStation> availableDestinations = getAvailableDestinations(lineId);
        
        if (availableDestinations == null) {
            log.error("Metro line not found: {}", lineId);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Metro line not found", player.getId(), null, false));
            return;
        }

        player.setState(State.TRAVELLING);
        final Map.Entry<String, List<MetroStation>> payload = Map.entry(lineId, availableDestinations);
        gameEventQueue.enqueue(
                GameEvent.create(EventType.OPEN_METRO_UI, payload, player.getId(), null, false)
        );
    }

    public void startTravel(Player player, String targetMapId, String lineId) {
        try {
            if (!worldManager.containsMap(targetMapId)) {
                log.error("Target map not found: {}", targetMapId);
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Travel failed: " + targetMapId, player.getId(), null, false));
                return;
            }
            final GameMapData targetMap = worldManager.getMap(targetMapId);
            var spawn = targetMap.getMetroSpawn(lineId);

            if (spawn != null) {
                player.setX(spawn.x());
                player.setY(spawn.y());
                player.setLayerIndex(spawn.z());

                player.setMapId(targetMapId);
                player.setState(State.ALIVE);

                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, targetMap, player.getId(), null, false));
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, targetMap.getObjects(), player.getId(), targetMapId, false));
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), null, false));
            } else {
                log.error("Metro spawn not found for map: {} and line: {}", targetMapId, lineId);
                player.setState(State.ALIVE);
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Travel failed: Destination spawn missing", player.getId(), null, false));
            }
        } catch (Exception e) {
            log.error("Error during travel to map: {}", targetMapId, e);
            player.setState(State.ALIVE);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Travel failed: " + e.getMessage(), player.getId(), null, false));
        }
    }

    public void completeTravel(Player player) {
        //TODO: zatim se nepouziva pozdeji pridam napr poslani eventu na spusteni cutsceny na klientovi
    }

    public List<MetroStation> getAvailableDestinations(String lineId) {
        return metroStations.get(lineId);
    }
}
