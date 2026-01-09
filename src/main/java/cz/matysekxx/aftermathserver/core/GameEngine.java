package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.GameSettings;
import cz.matysekxx.aftermathserver.core.logic.InteractionLogic;
import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player.State;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.*;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final Map<String, InteractionLogic> logicMap;
    private final GameSettings settings;

    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, Map<String, InteractionLogic> logicMap, GameSettings settings) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.logicMap = logicMap;
        this.settings = settings;
    }

    public void addPlayer(String sessionId) {
        final String mapId = settings.getStartingMapId() != null ? settings.getStartingMapId() : "hub_omega";

        final String className = settings.getDefaultClass(); //placeholder
        final GameSettings.PlayerClassConfig classConfig = settings.getClasses().get(className);
        final Player newPlayer = new Player(sessionId, "", settings.getSpawn().getX(), settings.getSpawn().getY(),
                classConfig.getMaxHp(),
                classConfig.getInventoryCapacity(),
                classConfig.getMaxWeight(),
                classConfig.getRadsLimit()
        );
        
        newPlayer.setId(sessionId);
        newPlayer.setMapId(mapId);
        newPlayer.setRole(className);
        players.put(sessionId, newPlayer);

        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, newPlayer, sessionId, mapId,false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_STATS, newPlayer, sessionId, mapId,false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, newPlayer, sessionId, mapId,false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, worldManager.getMap(mapId), sessionId, mapId,false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, worldManager.getMap(mapId).getObjects(), sessionId, mapId,false));
    }

    public void removePlayer(String sessionId) {
        players.remove(sessionId);
    }

    public String getPlayerMapId(String playerId) {
        final Player player = players.get(playerId);
        if (player == null) return null;
        return player.getMapId();
    }

    public Player processMove(String playerId, MoveRequest moveRequest) {
        final Player player = players.get(playerId);
        if (player == null) {
            return null;
        }

        int targetX = player.getX();
        int targetY = player.getY();

        switch (moveRequest.getDirection().toUpperCase()) {
            case "UP" -> targetY = player.getY() - 1;
            case "DOWN" -> targetY = player.getY() + 1;
            case "LEFT" -> targetX = player.getX() - 1;
            case "RIGHT" -> targetX = player.getX() + 1;
        }

        if (!canMoveTo(player, targetX, targetY)) {
            return null;
        }
        player.setX(targetX);
        player.setY(targetY);

        final GameMapData currentMap = worldManager.getMap(player.getMapId());
        final char symbolChar = currentMap.getLayer(player.getLayerIndex()).getSymbolAt(targetX, targetY);
        final String symbol = String.valueOf(symbolChar);

        if (currentMap.tileTriggerContains(symbol)) {
            TileTrigger trigger = currentMap.getTileTrigger(symbol);
            handleTileTrigger(player, trigger);
        }
        
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), player.getMapId(), false));
        return player;
    }

    private void handleTileTrigger(Player player, TileTrigger trigger) {
        switch (trigger.getType()) {
            case "METRO_TRAVEL" -> log.info("Player {} triggered metro travel with attribute {}", player.getId(), trigger.getAttribute());
            case "TELEPORT" -> {
                //TODO: implementovat teleport
            }
        }
    }
    public boolean canMoveTo(Player player, int targetX, int targetY) {
        return worldManager.isWalkable(
                player.getMapId(),
                player.getLayerIndex(),
                targetX,
                targetY
        );
    }

    public WebSocketResponse processInteract(String id, String targetObjectId) {
        final Player player = players.get(id);
        final GameMapData map = worldManager.getMap(player.getMapId());
        final MapObject target = map.getObject(targetObjectId);
        if (target == null) return WebSocketResponse.of("ACTION_FAILED", "Object not found");

        if (Math.abs(player.getX() - target.getX()) > 1 || Math.abs(player.getY() - target.getY()) > 1) {
            return WebSocketResponse.of("ACTION_FAILED", "You are too far away");
        }

        final InteractionLogic interactionLogic = logicMap.get(target.getAction());
        if (interactionLogic != null) {
            final WebSocketResponse response = interactionLogic.interact(target, player);
            switch (response.getType()) {
                case "LOOT_SUCCESS" -> gameEventQueue.enqueue(
                        GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
                case "MAP_LOAD" -> {
                    gameEventQueue.enqueue(
                        GameEvent.create(EventType.SEND_MAP_OBJECTS, worldManager.getMap(player.getMapId()).getObjects(), player.getId(), player.getMapId(), false));
                    gameEventQueue.enqueue(GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), player.getMapId(), false));
                }
            }
            return response;
        }
        return WebSocketResponse.of("ACTION_FAILED", "Action not found");
    }

    public WebSocketResponse dropItem(String playerId, int slotIndex, int amount) {
        final Player player = players.get(playerId);
        if (player == null) return WebSocketResponse.of("ERROR", "Player not found");

        final String droppedItemId = player.getInventory().removeItem(slotIndex, amount).getId();
        if (droppedItemId != null) {
            final GameMapData map = worldManager.getMap(player.getMapId());
            final MapObject lootBag = mapObjectFactory.createLootBag(droppedItemId, amount, player.getX(), player.getY());
            map.addObject(lootBag);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, map.getObjects(), null, player.getMapId(), true));
            return WebSocketResponse.of("DROP_SUCCESS", "Item dropped");
        }
        return WebSocketResponse.of("ACTION_FAILED", "Item not found or invalid amount");
    }

    @Scheduled(fixedRateString = "${game.tick-rate}")
    public void gameLoop() {
        updatePlayers();
    }

    private void updatePlayers() {
        for (Player player : players.values()) {
            if (player == null || player.getState() == State.DEAD) continue;

            final GameMapData map = worldManager.getMap(player.getMapId());
            if (map == null) continue;

            final Environment env = map.getEnvironment();
            boolean statsChanged = false;

            switch (map.getType()) {
                case MapType.HAZARD_ZONE -> {
                    if (env.getRadiation() > 0) {
                        player.setRads(player.getRads() + env.getRadiation());
                        if (player.getRads() > player.getRadsLimit()) {
                            player.setHp(player.getHp() - 1);
                            statsChanged = true;
                        }
                    }
                }
                case MapType.SAFE_ZONE -> {
                    if (player.getHp() < player.getMaxHp()) {
                        player.setHp(player.getHp() + 1);
                        statsChanged = true;
                    }
                    if (player.getRads() > 0) {
                        player.setRads(Math.max(0, player.getRads() - 5));
                        statsChanged = true;
                    }
                }
            }

            if (player.getHp() <= 0) {
                handlePlayerDeath(player);
                continue;
            }

            if (statsChanged || player.getRads() > 0) {
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_STATS, player, player.getId(), player.getMapId(), false));
            }
        }
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
}
