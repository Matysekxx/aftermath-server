package cz.matysekxx.aftermathserver.handler;


import cz.matysekxx.aftermathserver.command.Action;
import cz.matysekxx.aftermathserver.command.ChatAction;
import cz.matysekxx.aftermathserver.command.InteractAction;
import cz.matysekxx.aftermathserver.command.MoveAction;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final Map<String, Point> playerPositions = new ConcurrentHashMap<>();

    private final GameEngine gameEngine;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Action> actions = new HashMap<>();

    public GameHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        actions.put("MOVE", new MoveAction(gameEngine));
        actions.put("CHAT", new ChatAction());
        actions.put("INTERACT", new InteractAction(gameEngine));
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        gameEngine.removePlayer(session.getId());
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        gameEngine.addPlayer(session);
    }

    private void broadcast(WebSocketResponse response) {
        final String json = objectMapper.writeValueAsString(response);
        final TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(msg);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    private void  sendToSession(WebSocketResponse response, WebSocketSession session) {
        if (session.isOpen()) {
            final String json = objectMapper.writeValueAsString(response);
            final TextMessage msg = new TextMessage(json);
            try {
                session.sendMessage(msg);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        final WebSocketRequest request = objectMapper.convertValue(message.getPayload(), WebSocketRequest.class);
        final Action action = actions.get(request.getType());
        if (action != null) {
            final WebSocketResponse response = action.execute(session, request.getPayload());
            if (response.getType().equals("ACTION_FAILED")) {
                sendToSession(response, session);
            } else {
                broadcast(response);
            }
        }
    }
}
