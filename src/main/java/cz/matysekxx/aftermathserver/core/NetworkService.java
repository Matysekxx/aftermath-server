package cz.matysekxx.aftermathserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.MapLoadPayload;
import cz.matysekxx.aftermathserver.dto.PlayerUpdatePayload;
import cz.matysekxx.aftermathserver.dto.StatsResponse;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.handler.GameEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class NetworkService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<EventType, GameEventHandler> handlers = new EnumMap<>(EventType.class);
    private final Map<String, String> sessionToMap = new ConcurrentHashMap<>();
    private final GameEventQueue gameEventQueue;
    private ExecutorService eventLoopExecutor;

    public NetworkService(GameEventQueue gameEventQueue, List<GameEventHandler> gameEventHandlers) {
        this.gameEventQueue = gameEventQueue;
        for (GameEventHandler gameEventHandler : gameEventHandlers) {
            handlers.put(gameEventHandler.getType(), gameEventHandler);
        }
    }

    @PostConstruct
    public void startEventLoop() {
        final Runnable runnable = () -> {
           while (true) {
               try {
                   final GameEvent gameEvent = gameEventQueue.take();
                   if (handlers.containsKey(gameEvent.type())) {
                       handlers.get(gameEvent.type()).handleEvent(gameEvent);
                   }
               } catch (InterruptedException e) {
                   Thread.currentThread().interrupt();
                   log.error("Network thread interrupted", e);
                   break;
               } catch (Exception e) {
                   log.error("Error processing event", e);
               }
           }
        };
        eventLoopExecutor = Executors.newSingleThreadExecutor();
        eventLoopExecutor.execute(runnable);
    }

    @PreDestroy
    public void stopEventLoop() {
        if (eventLoopExecutor != null && !eventLoopExecutor.isShutdown()) {
            log.info("Stopping network event loop...");
            eventLoopExecutor.shutdownNow();
        }
    }

    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        sessionToMap.remove(sessionId);
    }

    public void updatePlayerLocation(String sessionId, String mapId) {
        sessionToMap.put(sessionId, mapId);
    }

    public void broadcastToMap(String payload, String mapId) {
        final TextMessage message = new TextMessage(payload);
        sessions.values().stream().filter(WebSocketSession::isOpen).forEach(session -> {
            final String currentMap = sessionToMap.get(session.getId());
            if (currentMap == null || !currentMap.equals(mapId)) return;
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                log.error(e.getMessage());
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
                log.error(e.getMessage());
            }
        }
    }

    public void sendStatsToClient(Player p) {
        final WebSocketSession session = sessions.get(p.getId());
        if (session != null && session.isOpen()) {
            try {
                final StatsResponse stats = StatsResponse.of(p);
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("STATS_UPDATE", stats));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error(e.getMessage());
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
                log.error(e.getMessage());
            }
        }
    }

    public void sendMapData(String sessionId, GameMapData map) {
        final WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                final MapLoadPayload payload = new MapLoadPayload(
                        map.getId(),
                        map.getName(),
                        map.getLayout(),
                        map.getEnvironment(),
                        map.getParsedLayers()
                );
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("MAP_LOAD", payload));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    public void broadcastMapObjects(List<MapObject> objects, String mapId) {
        try {
            broadcastToMap(objectMapper.writeValueAsString(WebSocketResponse.of("MAP_OBJECTS_UPDATE", objects)), mapId);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    public void sendPosition(Player p) {
        final WebSocketSession session = sessions.get(p.getId());
        if (session != null && session.isOpen()) {
            try {
                final PlayerUpdatePayload payload = new PlayerUpdatePayload(p.getId(), p.getX(), p.getY());
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of("PLAYER_MOVED", payload));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
