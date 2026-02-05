package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.*;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Spatial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/// Core engine managing the game state and loop.
///
/// Handles player management, movement, interactions, and the main game tick.
@Slf4j
@Service
public class GameEngine {
    /// Viewport constants for map rendering (radius from center)
    public static final int VIEWPORT_RANGE_X = 60;
    public static final int VIEWPORT_RANGE_Y = 20;
    private static final int TICKS_PER_DAY = 1200;
    /// Target density: 1 NPC per 1000 reachable tiles (0.001)
    private static final double NPC_DENSITY = 0.001;
    private static final int DAILY_RESPAWN_COUNT = 3;
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final MovementService movementService;
    private final StatsService statsService;
    private final InteractionService interactionService;
    private final EconomyService economyService;
    private final SpawnManager spawnManager;
    private final CombatService combatService;
    private final SpatialService spatialService;
    private final PlayerRegistry playerRegistry;
    private final LoginService loginService;
    private long tickCounter = 0;

    /// Constructs the GameEngine with all required services.
    ///
    /// @param worldManager       Manages game maps.
    /// @param gameEventQueue     Queue for game events.
    /// @param mapObjectFactory   Factory for creating map objects.
    /// @param movementService    Handles entity movement.
    /// @param statsService       Manages player statistics.
    /// @param interactionService Handles interactions.
    /// @param economyService     Manages economy and debts.
    /// @param spawnManager       Handles spawning of entities.
    /// @param combatService      Handles combat logic.
    /// @param spatialService     Manages spatial indexing.
    /// @param playerRegistry     Registry of active players.
    /// @param loginService       Handles login operations.
    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, MovementService movementService, StatsService statsService,
                      InteractionService interactionService, EconomyService economyService,
                      SpawnManager spawnManager, CombatService combatService, SpatialService spatialService,
                      PlayerRegistry playerRegistry, LoginService loginService) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.movementService = movementService;
        this.statsService = statsService;
        this.interactionService = interactionService;
        this.economyService = economyService;
        this.spawnManager = spawnManager;
        this.combatService = combatService;
        this.spatialService = spatialService;
        this.playerRegistry = playerRegistry;
        this.loginService = loginService;
    }

    /// Initializes world content such as NPCs.
    @EventListener(ApplicationReadyEvent.class)
    public void initializeWorld() {
        log.info("Initializing world content...");
        initialSpawnNpc();
        spawnItems();
    }

    /// Sends available login options (classes, maps) to the client.
    public void sendLoginOptions(String sessionId) {
        loginService.sendLoginOptions(sessionId);
    }

    /// Adds a new player session to the game.
    public void addPlayer(String sessionId, LoginRequest request) {
        loginService.handleLogin(sessionId, request);
    }

    /// Removes a player session.
    public void removePlayer(String sessionId) {
        playerRegistry.remove(sessionId);
    }

    /// Retrieves the map ID for a given player.
    public String getPlayerMapId(String playerId) {
        final Player player = playerRegistry.getPlayer(playerId);
        if (player == null) return null;
        return player.getMapId();
    }

    /// Processes a chat message request.
    public void handleChatMessage(ChatRequest chatData, String id) {
        playerRegistry.getMaybePlayer(id).ifPresent(player -> {
            gameEventQueue.enqueue(GameEventFactory.broadcastChatMsgEvent(chatData, player.getMapId()));
        });
    }

    /// Processes a movement request.
    public void processMove(String playerId, MoveRequest moveRequest) {
        playerRegistry.getMaybePlayer(playerId).ifPresent(player -> {
            movementService.movementProcess(player, moveRequest);
            broadcastMapPlayers(player.getMapId());
        });
    }

    /// Processes an interaction request.
    public void processInteract(String id) {
        playerRegistry.getMaybePlayer(id)
                .ifPresent(interactionService::processInteraction);
    }

    /// Handles dropping an item from inventory.
    public void dropItem(String playerId, int slotIndex, int amount) {
        final Optional<Player> maybePlayer = playerRegistry.getMaybePlayer(playerId);
        if (maybePlayer.isEmpty()) return;
        final Player player = maybePlayer.get();
        final Optional<Item> droppedItem = player.getInventory().removeItem(slotIndex, amount);
        droppedItem.ifPresentOrElse(item -> {
            final GameMapData map = worldManager.getMap(player.getMapId());
            final MapObject lootBag = mapObjectFactory.createLootBag(item.getId(), amount, player.getX(), player.getY(), player.getLayerIndex());
            map.addObject(lootBag);
            if (Objects.equals(slotIndex, player.getEquippedWeaponSlot())) player.setEquippedWeaponSlot(null);
            if (Objects.equals(slotIndex, player.getEquippedMaskSlot())) player.setEquippedMaskSlot(null);
            gameEventQueue.enqueue(GameEventFactory.sendInventoryEvent(player));
            gameEventQueue.enqueue(GameEventFactory.broadcastMapObjects(map.getObjects(), player.getMapId()));
        }, () -> gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Item not found or invalid amount", playerId)));
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

    private void broadcastMapPlayers(String mapId) {
        final List<OtherPlayerDto> players = new ArrayList<>();
        playerRegistry.forEachWithPredicate(
                p -> p.getMapId().equals(mapId) && p.getState() != State.DEAD && p.getState() != State.TRAVELLING,
                p -> players.add(OtherPlayerDto.fromPlayer(p))
        );

        if (players.size() > 1) {
            gameEventQueue.enqueue(GameEventFactory.broadcastPlayers(players, mapId));
        }
    }

    private void initialSpawnNpc() {
        worldManager.forEach(map -> {
            final int reachableTiles = spawnManager.getReachableTileCount(map.getId());
            if (map.getType() == MapType.HAZARD_ZONE) {
                final double difficultyMultiplier = 0.5 + (map.getDifficulty() * 0.5);
                final int maxNpcs = Math.max(5, (int) (reachableTiles * NPC_DENSITY * difficultyMultiplier));
                spawnManager.spawnRandomAggressiveNpcs(map.getId(), maxNpcs);
                log.info("Initial spawn on map {}: {} NPCs (based on {} tiles)", map.getId(), maxNpcs, reachableTiles);
            } else if (map.getType() == MapType.SAFE_ZONE) {
                int traderCount = Math.max(2, (int) (reachableTiles * NPC_DENSITY));
                spawnManager.spawnRandomTraderNpcs(map.getId(), traderCount);
                log.info("Initial spawn on safe map {}: {} Traders", map.getId(), traderCount);
            }
        });
    }

    private void respawnNpcs() {
        for (GameMapData map : worldManager.getMaps()) {
            final double difficultyMultiplier = 0.5 + (map.getDifficulty() * 0.5);
            final int reachableTiles = spawnManager.getReachableTileCount(map.getId());
            final int maxNpcs = Math.max(5, (int) (reachableTiles * NPC_DENSITY * difficultyMultiplier));
            final int currentCount = map.getNpcs().size();
            if (currentCount < maxNpcs) {
                final int toSpawn = Math.min(DAILY_RESPAWN_COUNT, maxNpcs - currentCount);
                switch (map.getType()) {
                    case SAFE_ZONE -> spawnManager.spawnRandomTraderNpcs(map.getId(), toSpawn);
                    case HAZARD_ZONE -> spawnManager.spawnRandomAggressiveNpcs(map.getId(), toSpawn);
                }
            }
        }
    }

    private void spawnItems() {
        worldManager.forEach(map -> {
            final int reachableTiles = spawnManager.getReachableTileCount(map.getId());
            final double density = map.getType() == MapType.HAZARD_ZONE ? (0.0005 * map.getDifficulty()) : 0.0001;
            int count = (int) (reachableTiles * density);
            if (map.getType() == MapType.SAFE_ZONE) {
                count = Math.max(4, count);
            } else {
                count = Math.max(8, count);
            }
            spawnManager.spawnRandomLoot(map.getId(), count);
            log.info("Spawned {} loot items on map: {} (density {})", count, map.getId(), density);
        });
    }

    private void updateNpcs(Set<String> activeMaps) {
        final Map<String, List<Player>> playersByMap = new HashMap<>();
        playerRegistry.forEach(p -> {
            playersByMap.computeIfAbsent(p.getMapId(), k -> new ArrayList<>()).add(p);
        });
        worldManager.forEachWithPredicate(map -> activeMaps.contains(map.getId()), map -> {
            final List<Player> playersOnMap = playersByMap.getOrDefault(map.getId(), List.of());
            map.getNpcs().forEach(npc -> npc.update(map, playersOnMap));

            final List<NpcDto> npcDtos = map.getNpcs().stream().map(NpcDto::fromEntity).collect(Collectors.toList());
            gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(npcDtos, map.getId()));

            final List<Spatial> allEntities = new ArrayList<>();
            allEntities.addAll(map.getNpcs());
            allEntities.addAll(playersOnMap);
            allEntities.addAll(map.getObjects());
            spatialService.rebuildIndex(map.getId(), allEntities);
        });
    }

    /// Updates the state of all active players.
    ///
    /// Applies environmental effects and checks for death conditions.
    private Set<String> updatePlayers() {
        final Set<String> activeMapIds = new HashSet<>();
        playerRegistry.forEachWithPredicate(
                player -> player.getState() != State.DEAD && player.getState() != State.TRAVELLING,
                player -> {
                    final boolean statsChanged = statsService.applyStats(player);
                    if (player.getHp() <= 0) {
                        handlePlayerDeath(player);
                    } else {
                        activeMapIds.add(player.getMapId());
                        if (statsChanged || player.getRads() > 0)
                            gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(player));
                    }
                }
        );
        return activeMapIds;
    }

    /// Handles the end-of-day logic.
    ///
    /// Triggers debt calculation for all players via EconomyService.
    private void processDailyCycle() {
        log.info("Processing daily cycle. Day: {}", tickCounter / TICKS_PER_DAY);
        playerRegistry.forEachWithPredicate(
                player -> player.getState() != State.DEAD,
                player -> {
                    economyService.processDailyDebt(player);
                    gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("A new day has dawned. Daily living fees have been deducted.", player.getId()));
                });
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

        final Optional<GameMapData> maybeMap = worldManager.getMaybeMap(player.getMapId());
        if (maybeMap.isEmpty()) return;
        final MapObject corpse = mapObjectFactory.createPlayerCorpse(player);
        maybeMap.get().addObject(corpse);

        player.getInventory().clear();
        player.setEquippedWeaponSlot(null);
        player.setEquippedMaskSlot(null);
        
        gameEventQueue.enqueue(GameEventFactory.sendGameOverEvent(player));

        sendLoginOptions(player.getId());
    }

    /// Retrieves a player instance by their unique session ID.
    ///
    /// @param playerId The session ID of the player.
    /// @return The Player object, or null if not found.
    public Optional<Player> getMaybePlayerById(String playerId) {
        return playerRegistry.getMaybePlayer(playerId);
    }

    /// Processes an attack request from a player.
    ///
    /// Delegates the combat logic to the CombatService using the player associated with the session.
    ///
    /// @param sessionId The session ID of the attacking player.
    public void processAttack(String sessionId) {
        playerRegistry.getMaybePlayer(sessionId).ifPresent(combatService::handleAttack);
    }

    /// Processes a request to use a consumable item.
    ///
    /// @param sessionId  The session ID of the player.
    /// @param useRequest The request details containing the item slot.
    public void processUse(String sessionId, UseRequest useRequest) {
        playerRegistry.getMaybePlayer(sessionId)
                .ifPresent(player -> statsService.useConsumable(player, useRequest));
    }

    /// Processes a request to equip an item.
    ///
    /// Handles equipping weapons or masks based on the item type in the specified slot.
    ///
    /// @param sessionId    The session ID of the player.
    /// @param equipRequest The request details containing the item slot.
    public void processEquip(String sessionId, EquipRequest equipRequest) {
        final Optional<Player> maybePlayer = playerRegistry.getMaybePlayer(sessionId);
        maybePlayer.ifPresent(player -> {
                    final Item item = player.getInventory().getSlots().get(equipRequest.getSlotIndex());
                    if (item != null) {
                        log.info("Player {} trying to equip item: {} (Type: {})", player.getName(), item.getName(), item.getType());
                        switch (item.getType()) {
                            case WEAPON -> {
                                player.setEquippedWeaponSlot(equipRequest.getSlotIndex());
                                gameEventQueue.enqueue(GameEventFactory.sendMessageEvent(
                                        "Equipped: " + item.getName(), sessionId));
                            }
                            case MASK -> {
                                player.setEquippedMaskSlot(equipRequest.getSlotIndex());
                                gameEventQueue.enqueue(GameEventFactory.sendMessageEvent(
                                        "Equipped Mask: " + item.getName(), sessionId));
                            }
                            default -> gameEventQueue.enqueue(GameEventFactory.sendErrorEvent(
                                    "Cannot equip this item", sessionId));
                        }
                    }
                }
        );
    }

    /// Processes a buy request from a player.
    ///
    /// Validates the player, map, and NPC before delegating the transaction to EconomyService.
    ///
    /// @param sessionId The session ID of the player.
    /// @param request   The buy request containing NPC ID and item index.
    public void processBuy(String sessionId, BuyRequest request) {
        final Optional<Player> player = playerRegistry.getMaybePlayer(sessionId);
        player.ifPresent(p -> {
                    final GameMapData map = worldManager.getMap(p.getMapId());
                    final Optional<Npc> npc = map.getNpcs().stream()
                            .filter(n -> n.getId().equals(request.getNpcId()))
                            .findFirst();
                    if (npc.isPresent()) {
                        economyService.processBuy(p, npc.get(), request);
                    } else {
                        gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Trader not found", sessionId));
                    }
                }
        );
    }

    public void processSell(String sessionId, SellRequest request) {
        playerRegistry.getMaybePlayer(sessionId).ifPresent(p -> {
            final GameMapData map = worldManager.getMap(p.getMapId());
            final Optional<Npc> npc = map.getNpcs().stream()
                    .filter(n -> n.getId().equals(request.getNpcId()))
                    .findFirst();
            if (npc.isPresent()) {
                economyService.processSell(p, npc.get(), request);
            } else {
                gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Trader not found", sessionId));
            }
        });
    }
}
