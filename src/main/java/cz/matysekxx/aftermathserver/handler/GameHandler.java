package cz.matysekxx.aftermathserver.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.action.Action;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.NetworkService;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class GameHandler extends TextWebSocketHandler {

    private final GameEngine gameEngine;
    
    private final NetworkService networkService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Action> actions;

    @Value("#{'${messaging.private-response-types}'.split(',')}")
    private Set<String> privateResponseTypes;

    public GameHandler(GameEngine gameEngine, NetworkService networkService, Map<String, Action> actions) {
        this.gameEngine = gameEngine;
        this.networkService = networkService;
        this.actions = actions;
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        networkService.removeSession(session.getId());
        gameEngine.removePlayer(session.getId());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        networkService.addSession(session);
        gameEngine.addPlayer(session.getId());
        networkService.updatePlayerLocation(session.getId(), gameEngine.getPlayerMapId(session.getId()));
    }

    public void broadcast(TextMessage message, String mapId) {
        networkService.broadcastToMap(message.getPayload(), mapId);
    }

    private void sendToSession(WebSocketSession session, TextMessage message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        try {
            final WebSocketRequest request = objectMapper.readValue(message.getPayload(), WebSocketRequest.class);
            final Action action = actions.get(request.getType());
            if (action != null) {
                final WebSocketResponse response = action.execute(session, request.getPayload());

                final String mapId = gameEngine.getPlayerMapId(session.getId());
                networkService.updatePlayerLocation(session.getId(), mapId);

                final TextMessage msg = new TextMessage(objectMapper.writeValueAsString(response));
                if (privateResponseTypes.contains(response.getType())) {
                    sendToSession(session, msg);
                } else if (mapId != null) {
                    broadcast(msg, mapId);
                }
            }
        } catch (Exception e) {
            log.error("Error while handling WebSocket request", e);
        }
    }
}