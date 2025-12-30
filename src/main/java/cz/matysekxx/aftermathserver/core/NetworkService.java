package cz.matysekxx.aftermathserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Service
public class NetworkService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(NetworkService.class);

    public NetworkService(GameEventQueue gameEventQueue) {
        final Runnable runnable = () -> {
           while (true) {
               try {
                   final GameEvent gameEvent = gameEventQueue.take();

                   switch (gameEvent.type()) {
                       case SEND_INVENTORY -> {
                           if (gameEvent.payload() instanceof Player player)
                                sendInventory(player);
                       }
                       case SEND_STATS -> {
                           if (gameEvent.payload() instanceof Player player)
                                sendStatsToClient(player);
                       }
                       case SEND_MAP_DATA ->{
                           if (gameEvent.payload() instanceof GameMapData gameMapData)
                               sendMapData(gameEvent.targetSessionId(), gameMapData);
                       }
                       case SEND_GAME_OVER -> {
                           if (gameEvent.payload() instanceof Player player)
                                sendGameOver(player);
                       }
                       case SEND_MAP_OBJECTS -> {
                           if (gameEvent.payload() instanceof List<?> list) {
                               @SuppressWarnings("unchecked")
                               List<MapObject> mapObjects = (List<MapObject>) list;
                               broadcastMapObjects(mapObjects);
                           }
                       }
                   }
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                   logger.error("Network thread interrupted", e);
                   break;
               } catch (Exception e) {
                   logger.error("Error processing event", e);
               }
           }
        };
        Executors.newSingleThreadExecutor().execute(runnable);
    }

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void broadcast(String payload) {
        final TextMessage message = new TextMessage(payload);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
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
                logger.error(e.getMessage());
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
                logger.error(e.getMessage());
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
                logger.error(e.getMessage());
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
                logger.error(e.getMessage());
            }
        }
    }

    public void broadcastMapObjects(List<MapObject> objects) {
        try {
            broadcast(objectMapper.writeValueAsString(WebSocketResponse.of("MAP_OBJECTS_UPDATE", objects)));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
    }
}
