package cz.matysekxx.aftermathserver.handler;


import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.Player;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final Map<String, Point> playerPositions = new ConcurrentHashMap<>();

    private final GameEngine gameEngine;

    private ObjectMapper objectMapper = new ObjectMapper();

    public GameHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        playerPositions.remove(session.getId());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        playerPositions.put(session.getId(), new Point());
    }

    private final void broadcast(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    sessions.remove(session);
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final WebSocketRequest request = objectMapper.convertValue(message.getPayload(), WebSocketRequest.class);
        switch (request.getType()) {
            case "MOVE" -> {
                final Player player = gameEngine.processMove(session.getId(), new MoveRequest(request.getPayload().get("direction").asString()));
            }
        }
    }
}
