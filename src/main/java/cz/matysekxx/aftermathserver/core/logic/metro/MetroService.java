package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.EconomyService;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/// Service responsible for handling metro system logic.
///
/// Manages station triggers, travel initiation, and available destinations.
@Slf4j
@Service
public class MetroService {
    private final Map<String, List<MetroStation>> metroStations;
    private final GameEventQueue gameEventQueue;
    private final WorldManager worldManager;
    private final EconomyService economyService;

    /// Initializes the MetroService with station data and dependencies.
    @Autowired
    public MetroService(@Qualifier("metroMapData") Map<String, List<MetroStation>> metroStations, GameEventQueue gameEventQueue, WorldManager worldManager, EconomyService economyService) {
        this.metroStations = metroStations;
        this.gameEventQueue = gameEventQueue;
        this.worldManager = worldManager;
        this.economyService = economyService;
        log.info("MetroService initialized");
    }

    /// Handles the event when a player steps on a metro trigger tile.
    ///
    /// Validates the line, updates player state to TRAVELLING, and opens the Metro UI on the client.
    ///
    /// @param player The player triggering the event.
    /// @param lineId The ID of the metro line.
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

    /// Initiates the travel process for a player to a target map.
    ///
    /// Validates the target map and spawn point. If valid, moves the player to the metro spawn point
    /// of the target map and sends updated map data.
    ///
    /// @param player      The player traveling.
    /// @param targetMapId The ID of the destination map.
    /// @param lineId      The ID of the metro line being used.
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
                final String startMapId = player.getMapId();
                final List<MetroStation> stations = metroStations.get(lineId);

                player.setX(spawn.x());
                player.setY(spawn.y());
                player.setLayerIndex(spawn.z());

                player.setMapId(targetMapId);
                player.setState(State.ALIVE);

                int startIndex = -1;
                int targetIndex = -1;

                for (int i = 0; i < stations.size(); i++) {
                    if (stations.get(i).getId().equals(startMapId)) startIndex = i;
                    if (stations.get(i).getId().equals(targetMapId)) targetIndex = i;
                }
                final int distance = (startIndex != -1 && targetIndex != -1) ? Math.abs(targetIndex - startIndex) : 1;
                economyService.recordActivityCost(player, 15 * Math.max(1, distance));
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, targetMap, player.getId(), null, false));
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, targetMap.getObjects(), player.getId(), targetMapId, false));

                final List<NpcDto> npcs = new ArrayList<>();
                for (Npc npc : targetMap.getNpcs()) {
                    final NpcDto npcDto = new NpcDto(npc.getId(), npc.getName(), npc.getType(), npc.getX(), npc.getY(), npc.getHp(), npc.getMaxHp(), npc.isAggressive());
                    npcs.add(npcDto);
                }
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_NPCS, npcs, player.getId(), targetMapId, false));

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

    /// Completes the travel process.
    ///
    /// Currently, a placeholder for future logic (e.g., cutscenes).
    public void completeTravel(Player player) {
        //TODO: zatim se nepouziva pozdeji pridam napr poslani eventu na spusteni cutsceny na klientovi
    }

    /// Retrieves a list of available stations for a given line.
    ///
    /// @param lineId The ID of the metro line.
    /// @return List of MetroStation objects or null if line not found.
    public List<MetroStation> getAvailableDestinations(String lineId) {
        return metroStations.get(lineId);
    }
}
