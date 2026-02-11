package cz.matysekxx.aftermathserver.websocket;


import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.action.Action;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import cz.matysekxx.aftermathserver.network.NetworkService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

/**
 * Main WebSocket handler for the game.
 * Manages player connections, disconnections, and routes incoming messages to actions.
 *
 * @author Matysekxx
 */
@Component
@Slf4j
public class GameHandler extends TextWebSocketHandler {
    private final GameEngine gameEngine;
    private final NetworkService networkService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Action> actions;

    /// Constructs the GameHandler with necessary dependencies.
    ///
    /// @param gameEngine     The core game engine.
    /// @param networkService The service for network communication.
    /// @param actions        A map of available game actions.
    public GameHandler(GameEngine gameEngine, NetworkService networkService, Map<String, Action> actions) {
        this.gameEngine = gameEngine;
        this.networkService = networkService;
        this.actions = actions;
    }

    /// Handles the cleanup when a WebSocket connection is closed.
    ///
    /// @param session The closed session.
    /// @param status  The reason for closing.
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        networkService.removeSession(session.getId());
        gameEngine.removePlayer(session.getId());
    }

    /// Handles the initialization when a new WebSocket connection is established.
    ///
    /// @param session The new session.
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        log.info("Connection established with session: {}", session.getId());
        networkService.addSession(session);
    }

    /// Processes incoming text messages from clients.
    ///
    /// @param session The session that sent the message.
    /// @param message The text message containing a JSON request.
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            final WebSocketRequest request = objectMapper.readValue(message.getPayload(), WebSocketRequest.class);
            log.info("Received request: {} from session: {}", request.getType(), session.getId());
            if (actions.containsKey(request.getType())) {
                final Action action = actions.get(request.getType());
                action.execute(session.getId(), request.getPayload());
                final String mapId = gameEngine.getPlayerMapId(session.getId());
                if (mapId != null) {
                    networkService.updatePlayerLocation(session.getId(), mapId);
                }
            } else {
                log.warn("Unknown action type: {}", request.getType());
            }
        } catch (Exception e) {
            log.error("Error while handling WebSocket request", e);
        }
    }
}