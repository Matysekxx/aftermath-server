package cz.matysekxx.aftermathserver.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.action.Action;
import cz.matysekxx.aftermathserver.action.ChatAction;
import cz.matysekxx.aftermathserver.action.DropAction;
import cz.matysekxx.aftermathserver.action.InteractAction;
import cz.matysekxx.aftermathserver.action.MoveAction;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.NetworkService;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class GameHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(GameHandler.class);

    private final GameEngine gameEngine;
    
    private final NetworkService networkService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Action> actions = new HashMap<>();


    private static final Set<String> SELF_ONLY_RESPONSES = Set.of(
            "ACTION_FAILED",
            "NOTIFICATION",
            "LOOT_SUCCESS",
            "MAP_LOAD"
    );

    public GameHandler(GameEngine gameEngine, NetworkService networkService) {
        this.gameEngine = gameEngine;
        this.networkService = networkService;
        actions.put("MOVE", new MoveAction(gameEngine));
        actions.put("CHAT", new ChatAction());
        actions.put("INTERACT", new InteractAction(gameEngine));
        actions.put("DROP", new DropAction(gameEngine));
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
    }

    public void broadcast(TextMessage message) {
        networkService.broadcast(message.getPayload());
    }

    private void sendToSession(WebSocketSession session, TextMessage message) {
        if (session.isOpen()) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                logger.error(e.getMessage());
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
                final TextMessage msg = new TextMessage(objectMapper.writeValueAsString(response));
                if (SELF_ONLY_RESPONSES.contains(response.getType())) {
                    sendToSession(session, msg);
                } else {
                    broadcast(msg);
                }
            }
        } catch (Exception e) {
            logger.error("Error while handling WebSocket request", e);
        }
    }
}