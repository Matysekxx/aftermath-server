package cz.matysekxx.aftermathserver.handler;


import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.Player;
import cz.matysekxx.aftermathserver.dto.GameDtos;
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

    private void broadcast(WebSocketResponse response) {
        final String json = objectMapper.writeValueAsString(response);
        final TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        final WebSocketRequest request = objectMapper.convertValue(message.getPayload(), WebSocketRequest.class);
        switch (request.getType()) {
            case "MOVE" -> {
                final String direction = String.valueOf(request.getPayload().get("direction"));
                final Player player = gameEngine.processMove(session.getId(), new GameDtos.MoveReq(direction));
                if (player != null) {
                    final var response = new GameDtos.PlayerUpdatePayload(
                            session.getId(),
                            player.getX(),
                            player.getY()
                    );
                    final String jsonResponse = objectMapper.writeValueAsString(response);
                    broadcast(WebSocketResponse.of("PLAYER_MOVED",jsonResponse));
                }
            }
            case "CHAT" -> {
                final var chatData = objectMapper.convertValue(message.getPayload(), GameDtos.ChatReq.class);
                broadcast(WebSocketResponse.of("CHAT_MSG",chatData));
            }
            case "ATTACK" -> {

            }
        }
    }
}
