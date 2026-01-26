package cz.matysekxx.aftermathserver.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.*;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/// Service responsible for WebSocket communication and event dispatching.
///
/// Manages active sessions, handles the main event loop, and sends messages to clients.
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

    /// Starts the background thread for processing events from the queue.
    @PostConstruct
    private void startEventLoop() {
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

    /// Stops the event processing loop.
    @PreDestroy
    private void stopEventLoop() {
        if (eventLoopExecutor != null && !eventLoopExecutor.isShutdown()) {
            log.info("Stopping network event loop...");
            eventLoopExecutor.shutdownNow();
        }
    }

    /// Registers a new WebSocket session.
    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    /// Removes a closed WebSocket session.
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        sessionToMap.remove(sessionId);
    }

    /// Updates the map ID associated with a session.
    public void updatePlayerLocation(String sessionId, String mapId) {
        sessionToMap.put(sessionId, mapId);
    }

    void broadcastToMap(String payload, String mapId) {
        final TextMessage message = new TextMessage(payload);
        sessions.values().stream().filter(WebSocketSession::isOpen).forEach(session -> {
            final String currentMap = sessionToMap.get(session.getId());
            if (currentMap == null || !currentMap.equals(mapId)) return;
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                log.error(e.getMessage());
            } catch (IllegalStateException e) {
                log.warn("Connection closed while broadcasting to {}: {}", session.getId(), e.getMessage());
                removeSession(session.getId());
            }
        });
    }

    void sendUIList(Map.Entry<String, List<MetroStation>> metroStations, String id) {
        final UILoadResponse dto = new UILoadResponse();
        dto.setLineId(metroStations.getKey());
        dto.setStations(metroStations.getValue());
        sendJson(id, "OPEN_METRO_UI", dto);
    }

    void sendToClient(String payload, String sessionId) {
        sendJson(sessionId, "NOTIFICATION", payload);
    }

    void sendGameOver(String sessionId) {
        sendJson(sessionId, "GAME_OVER", Map.of("message", "YOU DIED"));
    }

    void sendStatsToClient(Player p) {
        sendJson(p.getId(), "STATS_UPDATE", StatsResponse.of(p));
    }

    void sendInventory(Player p) {
        sendJson(p.getId(), "INVENTORY_UPDATE", p.getInventory().getSlots());
    }

    void sendMapData(String sessionId, MapViewportPayload payload) {
        sendJson(sessionId, "MAP_VIEWPORT", payload);
    }

    void broadcastMapObjects(List<MapObject> objects, String mapId) {
        try {
            broadcastToMap(objectMapper.writeValueAsString(WebSocketResponse.of("MAP_OBJECTS_UPDATE", objects)), mapId);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    void sendMapObjects(String sessionId, List<MapObject> objects) {
        sendJson(sessionId, "MAP_OBJECTS_UPDATE", objects);
    }

    void sendNpcs(String sessionId, String mapId, boolean isBroadcast, List<NpcDto> npcs) {
        try {
            final String json = objectMapper.writeValueAsString(WebSocketResponse.of("NPCS_UPDATE", npcs));
            if (isBroadcast) {
                broadcastToMap(json, mapId);
            } else sendToClient(json, sessionId);
        } catch (JsonProcessingException e) {
            log.error("Error serializing NPCs: {}", e.getMessage());
        }
    }

    void sendPosition(Player p) {
        sendJson(p.getId(), "PLAYER_MOVED", PlayerUpdatePayload.of(p));
    }

    void sendError(String sessionId, String message) {
        sendJson(sessionId, "ACTION_FAILED", message);
    }

    void sendLoginOptions(String sessionId, LoginOptionsResponse response) {
        sendJson(sessionId, "LOGIN_OPTIONS", response);
    }

    /// Helper method to serialize and send a message to a specific session.
    private void sendJson(String sessionId, String type, Object payload) {
        final WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                final String json = objectMapper.writeValueAsString(WebSocketResponse.of(type, payload));
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                log.error("Error sending message to {}: {}", sessionId, e.getMessage());
            } catch (IllegalStateException e) {
                log.warn("Connection closed while sending message to {}: {}", sessionId, e.getMessage());
                removeSession(sessionId);
            }
        } else {
            log.warn("Cannot send message to session {}: Session not found or closed", sessionId);
        }
    }
}
