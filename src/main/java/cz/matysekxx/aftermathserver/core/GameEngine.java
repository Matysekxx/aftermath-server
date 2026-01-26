package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.GameSettings;
import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.dto.*;
import cz.matysekxx.aftermathserver.dto.LoginRequest;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.dto.SpawnPointInfo;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// Core engine managing the game state and loop.
///
/// Handles player management, movement, interactions, and the main game tick.
@Slf4j
@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final GameSettings settings;
    private final MovementService movementService;
    private final StatsService statsService;
    private final InteractionService interactionService;
    private final EconomyService economyService;
    private final SpawnManager spawnManager;

    /// Viewport constants for map rendering (radius from center)
    public static final int VIEWPORT_RANGE_X = 60;
    public static final int VIEWPORT_RANGE_Y = 20;

    private long tickCounter = 0;
    private static final int TICKS_PER_DAY = 1200;

    /// Target density: 1 NPC per 100 reachable tiles (0.05)
    private static final double NPC_DENSITY = 0.05;
    private static final int DAILY_RESPAWN_COUNT = 3;

    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, GameSettings settings, MovementService movementService, StatsService statsService, InteractionService interactionService, EconomyService economyService, SpawnManager spawnManager) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.settings = settings;
        this.movementService = movementService;
        this.statsService = statsService;
        this.interactionService = interactionService;
        this.economyService = economyService;
        this.spawnManager = spawnManager;
    }

    /// Initializes world content such as NPCs.
    @PostConstruct
    public void initializeWorld() {
        log.info("Initializing world content...");
        initialSpawnNpc();
        spawnItems();
    }

    /// Sends available login options (classes, maps) to the client.
    public void sendLoginOptions(String sessionId) {
        log.info("Sending login options to session: {}", sessionId);
        final var classesMap = settings.getClasses();
        final List<String> classes = classesMap != null ? new ArrayList<>(classesMap.keySet()) : new ArrayList<>();
        final List<SpawnPointInfo> maps = new ArrayList<>();

        for (GameMapData map : worldManager.getMaps()) {
            if (map.getType() == MapType.SAFE_ZONE) {
                maps.add(new SpawnPointInfo(map.getId(), map.getName()));
                log.info("Adding safe zone map to login options: {}", map.getId());
            }
        }

        final LoginOptionsResponse response = new LoginOptionsResponse(classes, maps);
        log.info("Prepared LoginOptionsResponse: classes={}, maps={}", classes.size(), maps.size());
        log.info("Enqueuing SEND_LOGIN_OPTIONS event for session: {}", sessionId);
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_LOGIN_OPTIONS, response, sessionId, null, false));
    }

    /// Adds a new player session to the game.
    public void addPlayer(String sessionId, LoginRequest request) {
        if (players.containsKey(sessionId)) return;

        String mapId = request.getStartingMapId();
        if (mapId == null || !worldManager.containsMap(mapId)) {
            mapId = settings.getStartingMapId() != null ? settings.getStartingMapId() : "nemocnice-motol";
        }

        String className = request.getPlayerClass();
        if (className == null || settings.getClasses() == null || !settings.getClasses().containsKey(className)) {
            className = settings.getDefaultClass();
        }

        assert settings.getClasses() != null;
        final PlayerClassConfig classConfig = settings.getClasses().get(className);

        final GameMapData startingMap = worldManager.getMap(mapId);
        Vector3 spawn = startingMap.getMetroSpawn(settings.getLineId());
        if (spawn == null) spawn = new Vector3(10, 10, 0);

        final Player newPlayer = new Player(sessionId, request.getUsername(),
                spawn, classConfig, mapId, className
        );
        players.put(sessionId, newPlayer);

        enqueueViewport(newPlayer, startingMap);
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, worldManager.getMap(mapId).getObjects(), sessionId, mapId, false));

        final List<NpcDto> npcs = new ArrayList<>();
        for (Npc npc : worldManager.getMap(mapId).getNpcs()) {
            final NpcDto npcDto = NpcDto.fromEntity(npc);
            npcs.add(npcDto);
        }
        log.info("Sending {} NPCs to player {} on map {}", npcs.size(), newPlayer.getName(), mapId);
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_NPCS, npcs, sessionId, mapId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, newPlayer, sessionId, mapId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_STATS, newPlayer, sessionId, mapId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, newPlayer, sessionId, mapId, false));
    }

    /// Removes a player session.
    public void removePlayer(String sessionId) {
        players.remove(sessionId);
    }

    /// Retrieves the map ID for a given player.
    public String getPlayerMapId(String playerId) {
        final Player player = players.get(playerId);
        if (player == null) return null;
        return player.getMapId();
    }

    /// Processes a chat message request.
    public void handleChatMessage(ChatRequest chatData, String id) {
        gameEventQueue.enqueue(
                GameEvent.create(EventType.BROADCAST_CHAT_MSG, chatData, id, getPlayerMapId(id), true)
        );
    }

    /// Processes a movement request.
    public void processMove(String playerId, MoveRequest moveRequest) {
        movementService.movementProcess(players.get(playerId), moveRequest);
    }

    /// Processes an interaction request.
    public void processInteract(String id, String targetObjectId) {
        final Player player = players.get(id);
        final GameMapData map = worldManager.getMap(player.getMapId());
        final MapObject target = map.getObject(targetObjectId);
        interactionService.processInteraction(player, target);
    }

    /// Handles dropping an item from inventory.
    public void dropItem(String playerId, int slotIndex, int amount) {
        final Player player = players.get(playerId);
        final Optional<Item> droppedItem = player.getInventory().removeItem(slotIndex, amount);
        droppedItem.ifPresentOrElse(item -> {
            final GameMapData map = worldManager.getMap(player.getMapId());
            final MapObject lootBag = mapObjectFactory.createLootBag(item.getId(), amount, player.getX(), player.getY());
            map.addObject(lootBag);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, map.getObjects(), null, player.getMapId(), true));
        }, () -> gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Item not found or invalid amount", playerId, null, false)));
    }

    /// Main game loop executed periodically.
    @Scheduled(fixedRateString = "${game.tick-rate}")
    public void gameLoop() {
        tickCounter++;
        if (tickCounter % TICKS_PER_DAY == 0) {
            processDailyCycle();
        }
        final Set<String> activeMaps = updatePlayers();
        updateNpcs(activeMaps);
    }

    private void initialSpawnNpc() {
        for (GameMapData map : worldManager.getMaps()) {
            if (map.getType() == MapType.HAZARD_ZONE) {
                final int reachableTiles = spawnManager.getReachableTileCount(map.getId());
                final int maxNpcs = Math.max(5, (int) (reachableTiles * NPC_DENSITY));
                
                spawnManager.spawnRandomNpcs(map.getId(), maxNpcs);
                log.info("Initial spawn on map {}: {} NPCs (based on {} tiles)", map.getId(), maxNpcs, reachableTiles);
            }
        }
    }

    private void respawnNpcs() {
        for (GameMapData map : worldManager.getMaps()) {
            if (map.getType() == MapType.HAZARD_ZONE) {
                final int reachableTiles = spawnManager.getReachableTileCount(map.getId());
                final int maxNpcs = Math.max(5, (int) (reachableTiles * NPC_DENSITY));
                final int currentCount = map.getNpcs().size();
                
                if (currentCount < maxNpcs) {
                    int toSpawn = Math.min(DAILY_RESPAWN_COUNT, maxNpcs - currentCount);
                    spawnManager.spawnRandomNpcs(map.getId(), toSpawn);
                    log.info("Respawned {} NPCs on map {} (Limit: {})", toSpawn, map.getId(), maxNpcs);
                }
            }
        }
    }

    private void spawnItems() {
        for (GameMapData map : worldManager.getMaps()) {
            final double density = map.getType() == MapType.HAZARD_ZONE ? 0.005 : 0.001;
            spawnManager.spawnRandomLoot(map.getId(), density);
            log.info("Spawned loot on map: {} with density {}", map.getId(), density);
        }
    }

    private void updateNpcs(Set<String> activeMaps) {
        final Map<String, List<Player>> playersByMap = new HashMap<>();
        for (Player player : players.values()) {
            playersByMap.computeIfAbsent(player.getMapId(), k -> new ArrayList<>()).add(player);
        }

        for (GameMapData map : worldManager.getMaps()) {
            if (activeMaps.contains(map.getId())) {
                final List<Player> playersOnMap = playersByMap.getOrDefault(map.getId(), List.of());

                map.getNpcs().forEach(npc -> npc.update(map, playersOnMap));

                final List<NpcDto> npcDtos = new ArrayList<>();
                for (Npc npc : map.getNpcs()) {
                    final var npcDto = NpcDto.fromEntity(npc);
                    npcDtos.add(npcDto);
                }
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_NPCS, npcDtos, null, map.getId(), true));
            }
        }
    }

    /// Updates the state of all active players.
    ///
    /// Applies environmental effects and checks for death conditions.
    private Set<String> updatePlayers() {
        final Set<String> activeMapIds = new HashSet<>();
        for (Player player : players.values()) {
            if (player == null || player.getState() == State.DEAD || player.getState() == State.TRAVELLING) continue;
            final boolean statsChanged = statsService.applyStats(player);
            if (player.getHp() <= 0) {
                handlePlayerDeath(player);
                continue;
            }
            activeMapIds.add(player.getMapId());
            if (statsChanged || player.getRads() > 0)
                gameEventQueue.enqueue(
                        GameEvent.create(EventType.SEND_STATS, player, player.getId(), player.getMapId(), false));
        }
        return activeMapIds;
    }

    /// Handles the end-of-day logic.
    ///
    /// Triggers debt calculation for all players via EconomyService.
    private void processDailyCycle() {
        log.info("Processing daily cycle. Day: {}", tickCounter / TICKS_PER_DAY);
        for (Player player : players.values()) {
            if (player.getState() == State.DEAD) continue;
            economyService.processDailyDebt(player);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MESSAGE,
                    "A new day has dawned. Daily living fees have been deducted.", player.getId(), null, false));
        }
        respawnNpcs();
        spawnItems();
    }

    /// Handles the logic when a player's health reaches zero.
    ///
    /// Changes player state to DEAD, creates a lootable corpse object on the map,
    /// and clears the player's inventory.
    private void handlePlayerDeath(Player player) {
        if (player.getState() == State.DEAD) return;
        player.setState(State.DEAD);

        final GameMapData map = worldManager.getMap(player.getMapId());
        if (map == null) return;
        final MapObject corpse = mapObjectFactory.createPlayerCorpse(player);
        map.addObject(corpse);
        player.getInventory().clear();
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_GAME_OVER, player, player.getId(), player.getMapId(), false));
    }

    /// Helper to generate and enqueue a viewport update for a player.
    private void enqueueViewport(Player player, GameMapData mapData) {
        final MapViewportPayload viewport = MapViewportPayload.of(
                mapData, player.getX(), player.getY(), VIEWPORT_RANGE_X, VIEWPORT_RANGE_Y
        );
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, viewport, player.getId(), player.getMapId(), false));
    }

    /// Retrieves a player instance by their unique session ID.
    ///
    /// @param playerId The session ID of the player.
    /// @return The Player object, or null if not found.
    public Player getPlayerById(String playerId) {
        return players.get(playerId);
    }
}