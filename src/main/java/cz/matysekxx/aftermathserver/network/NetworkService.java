package cz.matysekxx.aftermathserver.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
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

/**
 * Service responsible for WebSocket communication and event dispatching.
 * Manages active sessions, handles the main event loop, and sends messages to clients.
 *
 * @author Matysekxx
 */
@Service
@Slf4j
public class NetworkService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<EventType, GameEventHandler> handlers = new EnumMap<>(EventType.class);
    private final Map<String, String> sessionToMap = new ConcurrentHashMap<>();
    private final GameEventQueue gameEventQueue;
    private ExecutorService eventLoopExecutor;

    /**
     * Constructs the NetworkService.
     *
     * @param gameEventQueue    The queue for game events.
     * @param gameEventHandlers A list of handlers for processing specific event types.
     */
    public NetworkService(GameEventQueue gameEventQueue, List<GameEventHandler> gameEventHandlers) {
        this.gameEventQueue = gameEventQueue;
        for (GameEventHandler gameEventHandler : gameEventHandlers) {
            handlers.put(gameEventHandler.getType(), gameEventHandler);
        }
    }

    /** Starts the background thread for processing events from the queue. */
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

    /** Stops the event processing loop. */
    @PreDestroy
    private void stopEventLoop() {
        if (eventLoopExecutor != null && !eventLoopExecutor.isShutdown()) {
            log.info("Stopping network event loop...");
            eventLoopExecutor.shutdownNow();
        }
    }

    /**
     * Registers a new WebSocket session.
     * @param session The session to add.
     */
    public void addSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    /**
     * Removes a closed WebSocket session.
     * @param sessionId The ID of the session to remove.
     */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
        sessionToMap.remove(sessionId);
    }

    /**
     * Updates the map ID associated with a session.
     * @param sessionId The session ID.
     * @param mapId     The new map ID.
     */
    public void updatePlayerLocation(String sessionId, String mapId) {
        sessionToMap.put(sessionId, mapId);
    }

    /**
     * Broadcasts a message to all players on a specific map.
     * @param payload The message payload.
     * @param mapId   The target map ID.
     */
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

    /** Broadcasts a message to all connected players regardless of their location. */
    void broadcastGlobalAnnouncement(String message) {
        try {
            final String json = objectMapper.writeValueAsString(WebSocketResponse.of("GLOBAL_ANNOUNCEMENT", message));
            final TextMessage textMessage = new TextMessage(json);
            sessions.values().stream()
                    .filter(WebSocketSession::isOpen)
                    .forEach(session -> {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("Error sending global announcement to {}: {}", session.getId(), e.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing global announcement: {}", e.getMessage());
        }
    }

    /** Sends the Metro UI data to a client. */
    void sendUIList(Map.Entry<String, List<MetroStation>> metroStations, String id) {
        final UILoadResponse dto = new UILoadResponse();
        dto.setLineId(metroStations.getKey());
        dto.setStations(metroStations.getValue());
        sendJson(id, "OPEN_METRO_UI", dto);
    }

    /** Sends a trade offer to a client to open the trading UI. */
    void sendTradeOffer(String sessionId, TradeOfferDto offer) {
        sendJson(sessionId, "OPEN_TRADE_UI", offer);
    }

    /** Sends a notification message to a client. */
    void sendToClient(String payload, String sessionId) {
        sendJson(sessionId, "SEND_MESSAGE", payload);
    }

    /** Sends the Game Over signal to a client. */
    void sendGameOver(String sessionId) {
        sendJson(sessionId, "SEND_GAME_OVER", Map.of("message", "YOU DIED"));
    }

    /** Sends updated player statistics to a client. */
    void sendStatsToClient(Player p) {
        sendJson(p.getId(), "SEND_STATS", StatsResponse.of(p));
    }

    /** Sends the player's inventory data to a client. */
    void sendInventory(Player p) {
        sendJson(p.getId(), "SEND_INVENTORY", p.getInventory().getSlots());
    }

    /** Sends the map viewport data to a client. */
    void sendMapData(String sessionId, MapViewportPayload payload) {
        sendJson(sessionId, "SEND_MAP_DATA", payload);
    }

    /** Broadcasts the list of map objects to all players on a map. */
    void broadcastMapObjects(List<MapObject> objects, String mapId) {
        try {
            broadcastToMap(objectMapper.writeValueAsString(WebSocketResponse.of("SEND_MAP_OBJECTS", objects)), mapId);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    /** Sends the list of map objects to a specific client. */
    void sendMapObjects(String sessionId, List<MapObject> objects) {
        sendJson(sessionId, "SEND_MAP_OBJECTS", objects);
    }

    /** Sends the list of NPCs to a client or broadcasts it to a map. */
    void sendNpcs(String sessionId, String mapId, boolean isBroadcast, List<NpcDto> npcs) {
        try {
            final String json = objectMapper.writeValueAsString(WebSocketResponse.of("SEND_NPCS", npcs));
            if (isBroadcast) {
                broadcastToMap(json, mapId);
            } else sendJson(sessionId, "SEND_NPCS", npcs);
        } catch (JsonProcessingException e) {
            log.error("Error serializing NPCs: {}", e.getMessage());
        }
    }

    /** Sends the player's updated position to the client. */
    void sendPosition(Player p) {
        sendJson(p.getId(), "SEND_PLAYER_POSITION", PlayerUpdatePayload.of(p));
    }

    void broadcastPlayers(List<OtherPlayerDto> players, String mapId) {
        try {
            broadcastToMap(objectMapper.writeValueAsString(
                    WebSocketResponse.of("BROADCAST_PLAYERS", players)
            ), mapId);
        } catch (JsonProcessingException e) {
            log.error("Error broadcasting players: {}", e.getMessage());
        }
    }

    /** Sends an error message to a client. */
    void sendError(String sessionId, String message) {
        sendJson(sessionId, "SEND_ERROR", message);
    }

    /** Sends the login options (classes, maps) to a client. */
    void sendLoginOptions(String sessionId, LoginOptionsResponse response) {
        sendJson(sessionId, "SEND_LOGIN_OPTIONS", response);
    }

    /** Helper method to serialize and send a message to a specific session. */
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
