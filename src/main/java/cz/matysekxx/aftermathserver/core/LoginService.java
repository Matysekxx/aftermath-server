package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.GameSettings;
import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.*;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static cz.matysekxx.aftermathserver.core.GameEngine.VIEWPORT_RANGE_X;
import static cz.matysekxx.aftermathserver.core.GameEngine.VIEWPORT_RANGE_Y;

/**
 * Service responsible for handling player authentication and initial game setup.
 * <p>
 * Manages the login process, including validating requests, selecting spawn points,
 * creating player instances, and sending initial game state to the client.
 *
 * @author Matysekxx
 */
@Service
@Slf4j
public class LoginService {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final GameSettings settings;
    private final PlayerRegistry playerRegistry;

    public LoginService(WorldManager worldManager, GameEventQueue gameEventQueue, GameSettings settings, PlayerRegistry playerRegistry) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.settings = settings;
        this.playerRegistry = playerRegistry;
    }

    /**
     * Sends available login options (classes, maps) to the client.
     *
     * @param sessionId The session ID of the connecting client.
     */
    public void sendLoginOptions(String sessionId) {
        log.info("Sending login options to session: {}", sessionId);
        final var classesMap = settings.getClasses();
        final List<String> classes = classesMap != null ? new ArrayList<>(classesMap.keySet()) : new ArrayList<>();
        final List<SpawnPointInfo> maps = new ArrayList<>();

        worldManager.forEachWithPredicate(
                mapData -> mapData.getType() == MapType.SAFE_ZONE,
                mapData -> {
                    maps.add(new SpawnPointInfo(mapData.getId(), mapData.getName()));
                    log.info("Adding safe zone map to login options: {}", mapData.getId());
                });
        final LoginOptionsResponse response = new LoginOptionsResponse(classes, maps);
        log.info("Prepared LoginOptionsResponse: classes={}, maps={}", classes.size(), maps.size());
        gameEventQueue.enqueue(GameEventFactory.sendLoginOptionsEvent(response, sessionId));
    }

    /**
     * Handles the player login process.
     * <p>
     * Validates the login request, creates a new player entity, registers it,
     * and triggers the initial data synchronization (viewport, inventory, stats).
     *
     * @param sessionId The session ID of the player.
     * @param request   The login data provided by the client.
     */
    public void handleLogin(String sessionId, LoginRequest request) {
        final Player existingPlayer = playerRegistry.getPlayer(sessionId);

        if (existingPlayer != null && existingPlayer.getState() != State.DEAD) {
            return;
        }

        final String mapId = resolveMapId(request.getStartingMapId());
        final String className = resolveClassName(request.getPlayerClass());
        final PlayerClassConfig classConfig = settings.getClasses().get(className);
        final GameMapData startingMap = worldManager.getMap(mapId);
        final Vector3 spawn = determineSpawnPoint(startingMap, request.getUsername());

        if (existingPlayer != null && existingPlayer.getState() == State.DEAD) {
            log.info("Respawning player {} (session: {})", request.getUsername(), sessionId);
            existingPlayer.setName(request.getUsername());
            existingPlayer.setRole(className);
            existingPlayer.setHp(classConfig.getMaxHp());
            existingPlayer.setMaxHp(classConfig.getMaxHp());
            existingPlayer.setX(spawn.x());
            existingPlayer.setY(spawn.y());
            existingPlayer.setLayerIndex(spawn.z());
            existingPlayer.setMapId(mapId);
            existingPlayer.setState(State.ALIVE);
            existingPlayer.setRads(0);
            existingPlayer.setRadsLimit(classConfig.getRadsLimit());
            
            sendInitialGameState(existingPlayer, startingMap);
        } else {
            final Player newPlayer = new Player(sessionId, request.getUsername(),
                    spawn, classConfig, mapId, className
            );
            playerRegistry.put(newPlayer);
            sendInitialGameState(newPlayer, startingMap);
        }
    }

    /**
     * Resolves the map ID, falling back to default if invalid.
     *
     * @param requestedMapId The map ID requested by the client.
     * @return A valid map ID.
     */
    private String resolveMapId(String requestedMapId) {
        if (requestedMapId != null && worldManager.containsMap(requestedMapId)) {
            return requestedMapId;
        }
        return settings.getStartingMapId() != null ? settings.getStartingMapId() : "nemocnice-motol";
    }

    /**
     * Resolves the class name, falling back to default if invalid.
     *
     * @param requestedClassName The class name requested by the client.
     * @return A valid class name.
     */
    private String resolveClassName(String requestedClassName) {
        if (requestedClassName != null && settings.getClasses() != null && settings.getClasses().containsKey(requestedClassName)) {
            return requestedClassName;
        }
        return settings.getDefaultClass();
    }

    /**
     * Determines the spawn point for a new player.
     *
     * @param map      The map data.
     * @param username The player's username.
     * @return A {@link Vector3} coordinate for spawning.
     */
    private Vector3 determineSpawnPoint(GameMapData map, String username) {
        final Vector3 metroSpawn = map.getMetroSpawn(settings.getLineId());
        if (metroSpawn != null) {
            return metroSpawn;
        }

        final Map<String, Vector3> availableSpawns = map.getSpawns();
        if (availableSpawns != null && !availableSpawns.isEmpty()) {
            final List<Vector3> spawnList = new ArrayList<>(availableSpawns.values());
            final Vector3 spawn = spawnList.get(ThreadLocalRandom.current().nextInt(spawnList.size()));
            log.info("Player {} spawning at random marker on map {}: {}", username, map.getId(), spawn);
            return spawn;
        }
        return new Vector3(10, 10, 0);
    }

    /**
     * Sends the initial game state to the client upon login.
     *
     * @param player The player entity.
     * @param map    The map data.
     */
    private void sendInitialGameState(Player player, GameMapData map) {
        enqueueViewport(player, map);
        gameEventQueue.enqueue(GameEventFactory.sendMapObjectsToPlayer(map.getObjects(), player.getId()));

        final List<NpcDto> npcs = map.getNpcs().stream().map(NpcDto::fromEntity).toList();
        log.info("Sending {} NPCs to player {} on map {}", npcs.size(), player.getName(), map.getId());
        gameEventQueue.enqueue(GameEventFactory.sendNpcsToPlayer(npcs, player.getId()));
        gameEventQueue.enqueue(GameEventFactory.sendInventoryEvent(player));
        gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
        gameEventQueue.enqueue(GameEventFactory.sendPositionEvent(player));
    }

    /**
     * Helper to generate and enqueue a viewport update for a player.
     *
     * @param player  The player entity.
     * @param mapData The map data.
     */
    private void enqueueViewport(Player player, GameMapData mapData) {
        final MapViewportPayload viewport = MapViewportPayload.of(
                mapData, player.getX(), player.getY(), player.getLayerIndex(), VIEWPORT_RANGE_X, VIEWPORT_RANGE_Y
        );

        if (viewport.getLayers().isEmpty()) {
            log.error("POZOR: Posílám PRÁZDNOU mapu pro hráče {}! (Z-index: {})", player.getName(), player.getLayerIndex());
        } else {
            log.info("Posílám mapu: {} (vrstev: {}, střed: [{},{},{}])", 
                    viewport.getMapName(), viewport.getLayers().size(), viewport.getCenterX(), viewport.getCenterY(), viewport.getCenterZ());
        }

        gameEventQueue.enqueue(GameEventFactory.sendMapDataEvent(viewport, player.getId()));
    }
}