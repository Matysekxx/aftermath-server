package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.dto.MoveRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameEngine {
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();

    public void addPlayer(WebSocketSession session) {
        final Player newPlayer = new Player();
        newPlayer.setId(session.getId());
        newPlayer.setSession(session);
        newPlayer.setX(10);
        newPlayer.setY(10);
        players.put(session.getId(), newPlayer);
    }

    public void removePlayer(String sessionId) {
        players.remove(sessionId);
    }

    public Player processMove(String playerId, MoveRequest moveRequest) {
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

    @Scheduled(fixedRate = 100)
    public void gameLoop() {
        //TODO: udelat game loop

    }

    public final Point getCurrentPlayerPosition(String id) {
        Player player = players.get(id);
        if (player != null) {
            return new Point(player.getX(), player.getY());
        }
        return null;
    }
}
