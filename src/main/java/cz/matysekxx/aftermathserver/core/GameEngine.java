package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.GameSettings;
import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.logic.interactions.InteractionLogic;
import cz.matysekxx.aftermathserver.core.logic.triggers.TriggerHandler;
import cz.matysekxx.aftermathserver.core.logic.triggers.TriggerRegistry;
import cz.matysekxx.aftermathserver.util.Coordination;
import cz.matysekxx.aftermathserver.util.Direction;
import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.State;
import cz.matysekxx.aftermathserver.core.world.*;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    private final Map<String, InteractionLogic> logicMap;
    private final GameSettings settings;
    private final TriggerRegistry triggerRegistry;

    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, Map<String, InteractionLogic> logicMap, GameSettings settings, TriggerRegistry triggerRegistry) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.logicMap = logicMap;
        this.settings = settings;
        this.triggerRegistry = triggerRegistry;
    }

    /// Adds a new player session to the game.
    public void addPlayer(String sessionId) {
        final String mapId = settings.getStartingMapId() != null ? settings.getStartingMapId() : "nemocnice-motol";

        final String className = settings.getDefaultClass(); //placeholder
        final PlayerClassConfig classConfig = settings.getClasses().get(className);
        
        final GameMapData startingMap = worldManager.getMap(mapId);
        final Coordination spawn = startingMap.getMetroSpawn(settings.getLineId());
        
        final Player newPlayer = new Player(sessionId, "", spawn.x(),spawn.y(),
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
        final Player player = players.get(playerId);
        if (player == null) {
            return;
        }

        int targetX = player.getX();
        int targetY = player.getY();

        final var dir = Direction.valueOf(moveRequest.getDirection().toUpperCase());
        targetX += dir.getDx();
        targetY += dir.getDy();

        if (!canMoveTo(player, targetX, targetY)) {
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "OBSTACLE", playerId, null, false));
            return;
        }
        player.setX(targetX);
        player.setY(targetY);

        final GameMapData currentMap = worldManager.getMap(player.getMapId());

        final int finalTargetX = targetX;
        final int finalTargetY = targetY;
        currentMap.getDynamicTrigger(targetX, targetY, player.getLayerIndex())
                .ifPresentOrElse(
                        trigger -> handleTileTrigger(player, trigger),
                        () -> currentMap.getMaybeTileTrigger(String.valueOf(currentMap.getLayer(player.getLayerIndex()).getSymbolAt(finalTargetX, finalTargetY)))
                                .ifPresent(trigger -> handleTileTrigger(player, trigger))
                );

        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), player.getMapId(), false));
    }

    private void handleTileTrigger(Player player, TileTrigger trigger) {
        final Optional<TriggerHandler> maybeTrigger = triggerRegistry.getHandler(trigger.getType());
        if (maybeTrigger.isEmpty()) log.error("trigger id null");
        maybeTrigger.ifPresent(triggerHandler -> triggerHandler.handle(player, trigger));
    }

    /// Checks if a player can move to target coordinates.
    public boolean canMoveTo(Player player, int targetX, int targetY) {
        return worldManager.isWalkable(
                player.getMapId(),
                player.getLayerIndex(),
                targetX,
                targetY
        );
    }

    /// Processes an interaction request.
    public void processInteract(String id, String targetObjectId) {
        final Player player = players.get(id);
        final GameMapData map = worldManager.getMap(player.getMapId());
        final MapObject target = map.getObject(targetObjectId);
        if (target == null) {
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Object not found", id, player.getMapId(), false));
            return;
        }

        if (Math.abs(player.getX() - target.getX()) > 1 || Math.abs(player.getY() - target.getY()) > 1) {
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "You are too far away", id, player.getMapId(), false));
        }

        final InteractionLogic interactionLogic = logicMap.get(target.getAction());
        if (interactionLogic != null) {
            final List<GameEvent> events = interactionLogic.interact(target, player);
            if (events != null) {
                events.forEach(gameEventQueue::enqueue);
            }
        }
    }

    /// Handles dropping an item from inventory.
    public void dropItem(String playerId, int slotIndex, int amount) {
        final Player player = players.get(playerId);
        if (player == null) {
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Player not found", playerId, null, false));
            return;
        }

        final Optional<Item> droppedItem = player.getInventory().removeItem(slotIndex, amount);
        if (droppedItem.isPresent()) {
            final GameMapData map = worldManager.getMap(player.getMapId());
            final MapObject lootBag = mapObjectFactory.createLootBag(droppedItem.get().getId(), amount, player.getX(), player.getY());
            map.addObject(lootBag);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, map.getObjects(), null, player.getMapId(), true));
            return;
        }
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Item not found or invalid amount", playerId, null, false));
    }

    /// Main game loop executed periodically.
    @Scheduled(fixedRateString = "${game.tick-rate}")
    public void gameLoop() {
        updatePlayers();
    }

    private void updatePlayers() {
        for (Player player : players.values()) {
            if (player == null || player.getState() == State.DEAD || player.getState() == State.TRAVELLING) continue;
            final GameMapData map = worldManager.getMap(player.getMapId());
            final Environment env = map.getEnvironment();
            boolean statsChanged = switch (map.getType()) {
                case MapType.HAZARD_ZONE -> applyRadiation(player, env);
                case MapType.SAFE_ZONE -> applyRegeneration(player);
            };
            if (player.getHp() <= 0) {
                handlePlayerDeath(player);
                continue;
            }
            if (statsChanged || player.getRads() > 0) gameEventQueue.enqueue(GameEvent.create(
                    EventType.SEND_STATS, player, player.getId(), player.getMapId(), false));
        }
    }

    private boolean applyRegeneration(Player player) {
        if (player.getHp() < player.getMaxHp()) {
            player.setHp(player.getHp() + 1);
            return true;
        }
        if (player.getRads() > 0) {
            player.setRads(Math.max(0, player.getRads() - 5));
            return true;
        }
        return false;
    }

    private boolean applyRadiation(Player player, Environment env) {
        if (env.getRadiation() > 0) {
            player.setRads(player.getRads() + env.getRadiation());
            if (player.getRads() > player.getRadsLimit()) {
                player.setHp(player.getHp() - 1);
                return true;
            }
        }
        return false;
    }

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

    public Player getPlayerById(String playerId) {
        return players.get(playerId);
    }
}