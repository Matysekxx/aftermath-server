package cz.matysekxx.aftermathserver.command;

import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.Player;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

public class MoveAction extends Action {
    private final GameEngine gameEngine;
    public MoveAction(GameEngine gameEngine) {
        super("PLAYER_MOVED");
        this.gameEngine = gameEngine;
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final GameDtos.MoveReq request = objectMapper.convertValue(payload, GameDtos.MoveReq.class);
        final String direction = request.getDirection();
        final Player player = gameEngine.processMove(session.getId(), new GameDtos.MoveReq(direction));
        if (player != null) {
            final var response = new GameDtos.PlayerUpdatePayload(
                    session.getId(),
                    player.getX(),
                    player.getY()
            );
            final String jsonResponse = objectMapper.writeValueAsString(response);
            return WebSocketResponse.of(type, jsonResponse);
        }
        return WebSocketResponse.of("ACTION_FAILED", "OBSTACLE");
    }
}
