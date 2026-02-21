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
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core engine managing the game state and loop.
 * Handles player management, movement, interactions, and the main game tick.
 *
 * @author Matysekxx
 */
@Slf4j
@Service
public class GameEngine {
    /**
     * Viewport constants for map rendering (radius from center)
     */
    public static final int VIEWPORT_RANGE_X = 60;
    public static final int VIEWPORT_RANGE_Y = 20;
    private static final int TICKS_PER_DAY = 1200;
    /**
     * Target density: 1 NPC per 1000 reachable tiles (0.001)
     */
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
    private final PlayerRegistry playerRegistry;
    private final LoginService loginService;
    private long tickCounter = 0;

    /**
     * Constructs the GameEngine with all required services.
     *
     * @param worldManager       Manages game maps.
     * @param gameEventQueue     Queue for game events.
     * @param mapObjectFactory   Factory for creating map objects.
     * @param movementService    Handles entity movement.
     * @param statsService       Manages player statistics.
     * @param interactionService Handles interactions.
     * @param economyService     Manages economy and debts.
     * @param spawnManager       Handles spawning of entities.
     * @param combatService      Handles combat logic.
     * @param playerRegistry     Registry of active players.
     * @param loginService       Handles login operations.
     */
    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, MovementService movementService, StatsService statsService,
                      InteractionService interactionService, EconomyService economyService,
                      SpawnManager spawnManager, CombatService combatService,
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
        this.playerRegistry = playerRegistry;
        this.loginService = loginService;
    }

    /**
     * Initializes world content such as NPCs and items upon application startup.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeWorld() {
        log.info("Initializing world content...");
        initialSpawnNpc();
        spawnItems();
    }

    /**
     * Sends available login options (classes, maps) to the client.
     *
     * @param sessionId The session ID of the client.
     */
    public void sendLoginOptions(String sessionId) {
        loginService.sendLoginOptions(sessionId);
    }

    /**
     * Adds a new player session to the game.
     *
     * @param sessionId The session ID.
     * @param request   The login request data.
     */
    public void addPlayer(String sessionId, LoginRequest request) {
        loginService.handleLogin(sessionId, request);
    }

    /**
     * Removes a player session from the registry.
     *
     * @param sessionId The session ID to remove.
     */
    public void removePlayer(String sessionId) {
        playerRegistry.remove(sessionId);
    }

    /**
     * Retrieves the map ID for a given player.
     *
     * @param playerId The session ID of the player.
     * @return The map ID or null if not found.
     */
    public String getPlayerMapId(String playerId) {
        final Player player = playerRegistry.getPlayer(playerId);
        if (player == null) return null;
        return player.getMapId();
    }

    /**
     * Processes a chat message request and broadcasts it to the map.
     *
     * @param chatData The chat request containing the message.
     * @param id       The session ID of the sender.
     */
    public void handleChatMessage(ChatRequest chatData, String id) {
        playerRegistry.getMaybePlayer(id).ifPresent(player ->
                gameEventQueue.enqueue(GameEventFactory.broadcastChatMsgEvent(chatData, player.getMapId())));
    }

    /**
     * Processes a movement request for a player.
     *
     * @param playerId    The session ID of the player.
     * @param moveRequest The movement request details.
     */
    public void processMove(String playerId, MoveRequest moveRequest) {
        playerRegistry.getMaybePlayer(playerId).ifPresent(player -> {
            movementService.movementProcess(player, moveRequest);
            broadcastMapPlayers(player.getMapId());
        });
    }

    /**
     * Processes an interaction request (looting, talking, etc.).
     *
     * @param id The session ID of the player.
     */
    public void processInteract(String id) {
        playerRegistry.getMaybePlayer(id)
                .ifPresent(interactionService::processInteraction);
    }

    /**
     * Handles dropping an item from inventory onto the map.
     *
     * @param playerId  The session ID of the player.
     * @param slotIndex The inventory slot index.
     * @param amount    The quantity to drop.
     */
    public void dropItem(String playerId, int slotIndex, int amount) {
        final Optional<Player> maybePlayer = playerRegistry.getMaybePlayer(playerId);
        if (maybePlayer.isEmpty()) return;
        final Player player = maybePlayer.get();
        final Item itemToCheck = player.getInventory().getSlots().get(slotIndex);
        if (itemToCheck != null && !itemToCheck.isDroppable()) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("This item cannot be dropped.", playerId));
            return;
        }
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

    /**
     * Main game loop executed periodically based on the configured tick rate.
     */
    @Scheduled(fixedRateString = "${game.tick-rate}")
    public void gameLoop() {
        tickCounter++;
        if (tickCounter % TICKS_PER_DAY == 0) {
            processDailyCycle();
        }
        final Set<String> activeMaps = updatePlayers();
        updateNpcs(activeMaps);
    }

    /**
     * Broadcasts the list of active players on a specific map to all players on that map.
     *
     * @param mapId The ID of the map to broadcast to.
     */
    private void broadcastMapPlayers(String mapId) {
        final List<OtherPlayerDto> players = new ArrayList<>();
        playerRegistry.forEachWithPredicate(
                p -> p.getMapId().equals(mapId) && p.getState() != State.DEAD,
                p -> players.add(OtherPlayerDto.fromPlayer(p))
        );

        if (players.size() > 1) {
            gameEventQueue.enqueue(GameEventFactory.broadcastPlayers(players, mapId));
        }
    }

    /**
     * Performs the initial spawning of NPCs across all maps based on map type and density.
     */
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

    /**
     * Replenishes NPC populations on maps that have fallen below their target density.
     */
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

    /**
     * Spawns random loot items across all maps based on map difficulty and type.
     */
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

    /**
     * Updates the AI logic for all NPCs on active maps.
     *
     * @param activeMaps The set of map IDs that currently have active players.
     */
    private void updateNpcs(Set<String> activeMaps) {
        final Map<String, List<Player>> playersByMap = new HashMap<>();
        playerRegistry.forEach(p ->
                playersByMap.computeIfAbsent(p.getMapId(), k -> new ArrayList<>()).add(p));
        worldManager.forEachWithPredicate(
                map -> activeMaps.contains(map.getId()), map -> {
                    final List<Player> playersOnMap = playersByMap.getOrDefault(map.getId(), List.of());
                    map.getNpcs().forEach(npc -> npc.update(map, playersOnMap));

                    final List<NpcDto> npcDtos = map.getNpcs().stream().map(NpcDto::fromEntity).collect(Collectors.toList());
                    gameEventQueue.enqueue(GameEventFactory.broadcastNpcs(npcDtos, map.getId()));
                });
    }

    /**
     * Updates the state of all active players.
     * Applies environmental effects and checks for death conditions.
     *
     * @return A set of map IDs that currently have active players.
     */
    private Set<String> updatePlayers() {
        final Set<String> activeMapIds = new HashSet<>();
        playerRegistry.forEachWithPredicate(
                player -> player.getState() != State.DEAD,
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

    /**
     * Handles the end-of-day logic.
     * Triggers debt calculation for all players via EconomyService.
     */
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

    /**
     * Handles the logic when a player's health reaches zero.
     * Changes player state to DEAD, creates a lootable corpse object on the map,
     * and clears the player's inventory.
     */
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

    /**
     * Retrieves a player instance by their unique session ID.
     *
     * @param playerId The session ID of the player.
     * @return An Optional containing the Player object.
     */
    public Optional<Player> getMaybePlayerById(String playerId) {
        return playerRegistry.getMaybePlayer(playerId);
    }

    /**
     * Processes an attack request from a player.
     * Delegates the combat logic to the CombatService.
     *
     * @param sessionId The session ID of the attacking player.
     */
    public void processAttack(String sessionId) {
        playerRegistry.getMaybePlayer(sessionId).ifPresent(combatService::handleAttack);
    }

    /**
     * Processes a request to use a consumable item.
     *
     * @param sessionId  The session ID of the player.
     * @param useRequest The request details containing the item slot.
     */
    public void processUse(String sessionId, UseRequest useRequest) {
        playerRegistry.getMaybePlayer(sessionId)
                .ifPresent(player -> statsService.useConsumable(player, useRequest));
    }

    /**
     * Processes a request to equip an item.
     * Handles equipping weapons or masks based on the item type.
     *
     * @param sessionId    The session ID of the player.
     * @param equipRequest The request details containing the item slot.
     */
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

    /**
     * Processes a buy request from a player.
     * Validates the player, map, and NPC before delegating to EconomyService.
     *
     * @param sessionId The session ID of the player.
     * @param request   The buy request containing NPC ID and item index.
     */
    public void processBuy(String sessionId, BuyRequest request) {
        playerRegistry.getMaybePlayer(sessionId).ifPresent(player ->
                findNpcOnPlayerMap(player, request.getNpcId()).ifPresentOrElse(
                        npc -> economyService.processBuy(player, npc, request),
                        () -> gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Trader not found", sessionId))
                ));
    }

    public void processSell(String sessionId, SellRequest request) {
        playerRegistry.getMaybePlayer(sessionId).ifPresent(player ->
                findNpcOnPlayerMap(player, request.getNpcId()).ifPresentOrElse(
                        npc -> economyService.processSell(player, npc, request),
                        () -> gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Trader not found", sessionId))
                ));
    }

    /**
     * Helper method to locate a specific NPC on the map where a player is currently located.
     *
     * @param player The player whose map should be searched.
     * @param npcId  The ID of the NPC to find.
     * @return An Optional containing the NPC if found.
     */
    private Optional<Npc> findNpcOnPlayerMap(Player player, String npcId) {
        return worldManager.getMaybeMap(player.getMapId())
                .flatMap(map -> map.getNpcs().stream()
                        .filter(n -> n.getId().equals(npcId))
                        .findFirst());
    }
}
