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
import cz.matysekxx.aftermathserver.dto.LoginOptionsResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private long tickCounter = 0;
    private static final int TICKS_PER_DAY = 1200;

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
        spawnNpc();
    }

    /// Sends available login options (classes, maps) to the client.
    public void sendLoginOptions(String sessionId) {
        final List<String> classes = new ArrayList<>(settings.getClasses().keySet());
        final List<SpawnPointInfo> maps = new ArrayList<>();

        final List<String> allowedMaps = settings.getSpawnableMaps() != null ? settings.getSpawnableMaps() : List.of("nemocnice-motol");

        for (String mapId : allowedMaps) {
            if (worldManager.containsMap(mapId)) {
                maps.add(new SpawnPointInfo(mapId, worldManager.getMap(mapId).getName()));
            }
        }

        final LoginOptionsResponse response = new LoginOptionsResponse(classes, maps);
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
        if (className == null || !settings.getClasses().containsKey(className)) {
            className = settings.getDefaultClass();
        }

        final PlayerClassConfig classConfig = settings.getClasses().get(className);

        final GameMapData startingMap = worldManager.getMap(mapId);
        Vector3 spawn = startingMap.getMetroSpawn(settings.getLineId());
        if (spawn == null) spawn = new Vector3(10, 10, 0);

        final Player newPlayer = new Player(
                sessionId,
                request.getUsername(),
                spawn.x(),
                spawn.y(),
                classConfig.getMaxHp(),
                classConfig.getInventoryCapacity(),
                classConfig.getMaxWeight(),
                classConfig.getRadsLimit()
        );
        newPlayer.setLayerIndex(spawn.z());
        newPlayer.setId(sessionId);
        newPlayer.setMapId(mapId);
        newPlayer.setRole(className);
        players.put(sessionId, newPlayer);

        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, worldManager.getMap(mapId), sessionId, mapId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, worldManager.getMap(mapId).getObjects(), sessionId, mapId, false));

        final List<NpcDto> npcs = new ArrayList<>();
        for (Npc npc : worldManager.getMap(mapId).getNpcs()) {
            final NpcDto npcDto = new NpcDto(npc.getId(), npc.getName(), npc.getType(), npc.getX(), npc.getY(), npc.getHp(), npc.getMaxHp(), npc.isAggressive());
            npcs.add(npcDto);
        }

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
        updatePlayers();
    }

    private void spawnNpc() {
        for (GameMapData map : worldManager.getMaps()) {
            if (map.getType() == MapType.HAZARD_ZONE) {
                spawnManager.spawnRandomNpcs(map.getId(), 5);
                log.info("Spawned NPCs on map: {}", map.getId());
            }
        }
    }

    /// Updates the state of all active players.
    ///
    /// Applies environmental effects and checks for death conditions.
    private void updatePlayers() {
        for (Player player : players.values()) {
            if (player == null || player.getState() == State.DEAD || player.getState() == State.TRAVELLING) continue;
            final boolean statsChanged = statsService.applyStats(player);
            if (player.getHp() <= 0) {
                handlePlayerDeath(player);
                continue;
            }
            if (statsChanged || player.getRads() > 0)
                gameEventQueue.enqueue(
                        GameEvent.create(EventType.SEND_STATS, player, player.getId(), player.getMapId(), false));
        }
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

    /// Retrieves a player instance by their unique session ID.
    ///
    /// @param playerId The session ID of the player.
    /// @return The Player object, or null if not found.
    public Player getPlayerById(String playerId) {
        return players.get(playerId);
    }
}