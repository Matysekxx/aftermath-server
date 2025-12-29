package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;

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
            return WebSocketResponse.of(type, response);
        }
        return WebSocketResponse.of("ACTION_FAILED", "OBSTACLE");
    }
}
