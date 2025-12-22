package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.event.InteractEvent;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final Map<String, InteractEvent> interactEvents = new HashMap<>();

    public GameEngine(WorldManager worldManager) {
        this.worldManager = worldManager;
        interactEvents.put("READ", new InteractEvent.ReadEvent());
        interactEvents.put("LOOT", new InteractEvent.LootEvent());
        interactEvents.put("TRAVEL", new InteractEvent.TravelEvent(worldManager));
    }

    public void addPlayer(WebSocketSession session) {
        final Player newPlayer = new Player();
        newPlayer.setId(session.getId());
        newPlayer.setSession(session);
        newPlayer.setX(10);
        newPlayer.setY(10);
        players.put(session.getId(), newPlayer);
    }

    public final Player getPlayer(WebSocketSession session) {
        return players.get(session.getId());
    }

    public void removePlayer(String sessionId) {
        players.remove(sessionId);
    }

    public Player processMove(String playerId, GameDtos.MoveReq moveRequest) {
        final Player player = players.get(playerId);
        if (player != null) {
            switch (moveRequest.getDirection().toUpperCase()) {
                case "UP" -> player.setY(player.getY() + 1);
                case "DOWN" -> player.setY(player.getY() - 1);
                case "LEFT" -> player.setX(player.getX() - 1);
                case "RIGHT" -> player.setX(player.getX() + 1);
            }
            return player;
        }
        return null;
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

        final InteractEvent interactEvent = interactEvents.get(target.getAction());
        if (interactEvent != null) {
            return interactEvent.eventIn(target, player);
        }
        return WebSocketResponse.of("ACTION_FAILED", "Action not found");
    }

    @Scheduled(fixedRate = 100)
    public void gameLoop() {
        //TODO: udelat game loop
    }

    public final Point getCurrentPlayerPosition(String id) {
        final Player player = players.get(id);
        if (player != null) {
            return new Point(player.getX(), player.getY());
        }
        return null;
    }
}
