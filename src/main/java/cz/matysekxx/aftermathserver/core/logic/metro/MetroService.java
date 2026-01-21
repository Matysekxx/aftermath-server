package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.util.Tuple;
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
        final Tuple<String, List<MetroStation>> payload = Tuple.of(lineId, availableDestinations);
        gameEventQueue.enqueue(
                GameEvent.create(EventType.OPEN_METRO_UI, payload, player.getId(), null, false)
        );
    }

    public void startTravel(Player  player, String targetMapId) {
        final GameMapData targetMap = worldManager.getMap(targetMapId);

        var spawn = targetMap.getSpawn();
        player.setX(spawn.z());
        player.setY(spawn.y());
        player.setLayerIndex(spawn.z());
        
        player.setMapId(targetMapId);
        //TODO: dodelat odeslani evenu

        /*
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA)
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS)
         */
    }

    public void completeTravel(Player player) {
        //TODO: zatim se nepouziva pozdeji pridam napr poslani eventu na spusteni cutsceny na klientovi
    }

    public List<MetroStation> getAvailableDestinations(String lineId) {
        return metroStations.get(lineId);
    }
}
