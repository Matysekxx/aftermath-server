package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Inventory;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.*;
import cz.matysekxx.aftermathserver.event.InteractEvent;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.awt.Point;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final WorldManager worldManager;
    private final Map<String, InteractEvent> interactEvents = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameEngine(WorldManager worldManager) {
        this.worldManager = worldManager;
        interactEvents.put("READ", new InteractEvent.ReadEvent());
        interactEvents.put("LOOT", new InteractEvent.LootEvent());
        interactEvents.put("TRAVEL", new InteractEvent.TravelEvent(worldManager));
    }

    public void addPlayer(WebSocketSession session) {
        final GameMapData startingMap = worldManager.getStartingMap();
        final String mapId = startingMap != null ? startingMap.getId() : "hub_omega";
        
        final Player newPlayer = new Player(
                session.getId(), "", mapId, 50, 15, 0, new Inventory(10, 10f), 100, 100, "", 0, 20, session
        );
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

        final InteractEvent interactEvent = interactEvents.get(target.getAction());
        if (interactEvent != null) {
            return interactEvent.eventIn(target, player);
        }
        return WebSocketResponse.of("ACTION_FAILED", "Action not found");
    }

    @Scheduled(fixedRate = 1000)
    public void gameLoop() {
        updatePlayers();
    }

    private void updatePlayers() {
        for (Player player : players.values()) {
            if (player == null || player.isDead()) continue;

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
                //TODO: pridat reseni smrti
                continue;
            }

            if (statsChanged || player.getRads() > 0) {
                sendStatsToClient(player);
            }
        }
    }

    private void sendStatsToClient(Player p) {
        if (p.getSession().isOpen()) {
            try {
                final GameDtos.StatsResponse stats = new GameDtos.StatsResponse(
                        p.getHp(),
                        p.getMaxHp(),
                        p.getRads()
                );

                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("STATS_UPDATE", stats));
                p.getSession().sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final Point getCurrentPlayerPosition(String id) {
        final Player player = players.get(id);
        if (player != null) {
            return new Point(player.getX(), player.getY());
        }
        return null;
    }
}
