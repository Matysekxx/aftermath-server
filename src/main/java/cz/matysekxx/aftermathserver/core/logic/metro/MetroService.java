package cz.matysekxx.aftermathserver.core.logic.metro;

import cz.matysekxx.aftermathserver.core.EconomyService;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.MapViewportPayload;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for managing the metro transportation system logic.
 * <p>
 * Handles station triggers, travel initiation between maps, and destination management.
 *
 * @author Matysekxx
 */
@Slf4j
@Service
public class MetroService {
    /** Map of metro line IDs to their list of stations. */
    private final Map<String, List<MetroStation>> metroStations;
    /** Queue for dispatching game events. */
    private final GameEventQueue gameEventQueue;
    /** Manager for world and map data. */
    private final WorldManager worldManager;
    /** Service for handling economic transactions. */
    private final EconomyService economyService;

    /**
     * Initializes the MetroService with station data and dependencies.
     */
    @Autowired
    public MetroService(@Qualifier("metroMapData") Map<String, List<MetroStation>> metroStations, GameEventQueue gameEventQueue, WorldManager worldManager, EconomyService economyService) {
        this.metroStations = metroStations;
        this.gameEventQueue = gameEventQueue;
        this.worldManager = worldManager;
        this.economyService = economyService;
        log.info("MetroService initialized");
    }

    /**
     * Handles the event when a player steps on a metro trigger tile.
     * <p>
     * Validates the line, updates player state to TRAVELLING, and opens the Metro UI on the client.
     *
     * @param player The player triggering the event.
     * @param lineId The ID of the metro line.
     */
    public void handleStationTrigger(Player player, String lineId) {
        final List<MetroStation> availableDestinations = getAvailableDestinations(lineId);

        if (availableDestinations == null) {
            log.error("Metro line not found: {}", lineId);
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Metro line not found", player.getId()));
            return;
        }

        player.setState(State.TRAVELLING);
        final Map.Entry<String, List<MetroStation>> payload = Map.entry(lineId, availableDestinations);
        gameEventQueue.enqueue(
                GameEventFactory.sendMetroUiEvent(payload, player.getId())
        );
    }

    /**
     * Initiates the travel process for a player to a target map.
     * <p>
     * Validates the target map and spawn point. If valid, moves the player to the metro spawn point
     * of the target map and sends updated map data.
     *
     * @param player      The player traveling.
     * @param targetMapId The ID of the destination map.
     * @param lineId      The ID of the metro line being used.
     */
    public void startTravel(Player player, String targetMapId, String lineId) {
        try {
            if (!worldManager.containsMap(targetMapId)) {
                log.error("Target map not found: {}", targetMapId);
                gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Travel failed: " + targetMapId, player.getId()));
                return;
            }
            final GameMapData targetMap = worldManager.getMap(targetMapId);
            final Vector3 spawn = targetMap.getMetroSpawn(lineId);

            if (spawn != null) {
                final String startMapId = player.getMapId();
                final List<MetroStation> stations = metroStations.get(lineId);

                player.setX(spawn.x());
                player.setY(spawn.y());
                player.setLayerIndex(spawn.z());
                player.setHp(player.getMaxHp());
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

                final var viewport = MapViewportPayload.of(
                        targetMap, player.getX(), player.getY(), player.getLayerIndex(), GameEngine.VIEWPORT_RANGE_X, GameEngine.VIEWPORT_RANGE_Y
                );
                gameEventQueue.enqueue(GameEventFactory.sendMapDataEvent(viewport, player.getId()));

                gameEventQueue.enqueue(GameEventFactory.sendMapObjectsToPlayer(targetMap.getObjects(), player.getId()));

                final List<NpcDto> npcs = new ArrayList<>();
                for (Npc npc : targetMap.getNpcs()) {
                    final NpcDto npcDto = NpcDto.fromEntity(npc);
                    npcs.add(npcDto);
                }
                gameEventQueue.enqueue(GameEventFactory.sendNpcsToPlayer(npcs, player.getId()));

                gameEventQueue.enqueue(GameEventFactory.sendPositionEvent(player));
            } else {
                log.error("Metro spawn not found for map: {} and line: {}", targetMapId, lineId);
                player.setState(State.ALIVE);
                gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Travel failed: Destination spawn missing", player.getId()));
            }
        } catch (Exception e) {
            log.error("Error during travel to map: {}", targetMapId, e);
            player.setState(State.ALIVE);
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Travel failed: " + e.getMessage(), player.getId()));
        }
    }

    /**
     * Completes the travel process.
     * <p>
     * Currently, a placeholder for future logic (e.g., cutscenes).
     */
    public void completeTravel(Player player) {
        //TODO: zatim se nepouziva pozdeji pridam napr poslani eventu na spusteni cutsceny na klientovi
    }

    /**
     * Retrieves a list of available stations for a given line.
     *
     * @param lineId The ID of the metro line.
     * @return List of MetroStation objects or null if line not found.
     */
    public List<MetroStation> getAvailableDestinations(String lineId) {
        return metroStations.get(lineId);
    }
}
