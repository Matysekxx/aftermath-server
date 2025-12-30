package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player.State;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.*;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final Map<String, InteractionLogic> logicMap = new HashMap<>();

    public GameEngine(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        logicMap.put("READ", new InteractionLogic.ReadLogic());
        logicMap.put("LOOT", new InteractionLogic.LootLogic());
        logicMap.put("TRAVEL", new InteractionLogic.TravelLogic(worldManager));
    }

    public void addPlayer(String sessionId) {
        final GameMapData startingMap = worldManager.getStartingMap();
        final String mapId = startingMap != null ? startingMap.getId() : "hub_omega";
        
        final Player newPlayer = new Player(sessionId, ""); //placeholder
        newPlayer.setId(sessionId);
        newPlayer.setCurrentMapId(mapId);
        players.put(sessionId, newPlayer);

        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, newPlayer, sessionId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_STATS, newPlayer, sessionId, false));
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_DATA, worldManager.getMap(mapId), sessionId, false));
    }

    public void removePlayer(String sessionId) {
        players.remove(sessionId);
    }

    public Player processMove(String playerId, GameDtos.MoveReq moveRequest) {
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
        return player;
    }

    public boolean canMoveTo(Player player, int targetX, int targetY) {
        return worldManager.isWalkable(
                player.getCurrentMapId(),
                player.getCurrentLayer(),
                targetX,
                targetY
        );
    }

    public WebSocketResponse processInteract(String id, String targetObjectId) {
        final Player player = players.get(id);
        final GameMapData map = worldManager.getMap(player.getCurrentMapId());
        final MapObject target = map.getObjects()
                .stream()
                .filter(obj -> obj.getId().equals(targetObjectId))
                .findFirst()
                .orElse(null);
        if (target == null) return WebSocketResponse.of("ACTION_FAILED", "Object not found");

        if (Math.abs(player.getX() - target.getX()) > 1 || Math.abs(player.getY() - target.getY()) > 1) {
            return WebSocketResponse.of("ACTION_FAILED", "You are too far away");
        }

        final InteractionLogic interactionLogic = logicMap.get(target.getAction());
        if (interactionLogic != null) {
            return interactionLogic.interact(target, player);
        }
        return WebSocketResponse.of("ACTION_FAILED", "Action not found");
    }

    public WebSocketResponse dropItem(String playerId, int slotIndex, int amount) {
        final Player player = players.get(playerId);
        if (player == null) return WebSocketResponse.of("ERROR", "Player not found");

        final Item droppedItem = player.getInventory().removeItem(slotIndex, amount);
        if (droppedItem != null) {
            final GameMapData map = worldManager.getMap(player.getCurrentMapId());
            final MapObject lootBag = mapObjectFactory.createLootBag(droppedItem, player.getX(), player.getY());
            map.getObjects().add(lootBag);
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), false));
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_MAP_OBJECTS, map.getObjects(), null, true));
            return WebSocketResponse.of("DROP_SUCCESS", "Item dropped");
        }
        return WebSocketResponse.of("ACTION_FAILED", "Item not found or invalid amount");
    }

    @Scheduled(fixedRate = 250)
    public void gameLoop() {
        updatePlayers();
    }

    private void updatePlayers() {
        for (Player player : players.values()) {
            if (player == null || player.getState() == State.DEAD) continue;

            final GameMapData map = worldManager.getMap(player.getCurrentMapId());
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
                gameEventQueue.enqueue(GameEvent.create(EventType.SEND_STATS, player, player.getId(), false));
            }
        }
    }

    private void handlePlayerDeath(Player player) {
        if (player.getState() == State.DEAD) return;
        player.setState(State.DEAD);

        final GameMapData map = worldManager.getMap(player.getCurrentMapId());
        if (map == null) return;
        final MapObject corpse = mapObjectFactory.createPlayerCorpse(player);
        map.getObjects().add(corpse);
        player.getInventory().clear();
        gameEventQueue.enqueue(GameEvent.create(EventType.SEND_GAME_OVER, player, player.getId(), false));
    }

    public final Point getCurrentPlayerPosition(String id) {
        final Player player = players.get(id);
        if (player != null) {
            return new Point(player.getX(), player.getY());
        }
        return null;
    }
}
