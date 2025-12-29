package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NetworkService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void broadcast(String payload) {
        final TextMessage message = new TextMessage(payload);
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Broadcast error: " + e.getMessage());
                }
            }
        });
    }

    public void sendGameOver(Player p) {
        final WebSocketSession session = sessions.get(p.getId());
        if (session != null && session.isOpen()) {
            final TreeMap<String, Object> gameOverData = new TreeMap<>();
            gameOverData.put("message", "YOU DIED");
            gameOverData.put("respawn_possible", true);
            try {
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("GAME_OVER", gameOverData));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendStatsToClient(Player p) {
        final WebSocketSession session = sessions.get(p.getId());
        if (session != null && session.isOpen()) {
            try {
                final GameDtos.StatsResponse stats = GameDtos.StatsResponse.of(p);
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("STATS_UPDATE", stats));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendInventory(Player p) {
        final WebSocketSession session = sessions.get(p.getId());
        if (session != null && session.isOpen()) {
            try {
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("INVENTORY_UPDATE", p.getInventory().getSlots()));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendMapData(String sessionId, GameMapData map) {
        final WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("MAP_LOAD", map));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void broadcastMapObjects(List<MapObject> objects) {
        broadcast(objectMapper.writeValueAsString(WebSocketResponse.of("MAP_OBJECTS_UPDATE", objects)));
    }
}
