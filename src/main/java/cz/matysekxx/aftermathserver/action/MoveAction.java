package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.dto.PlayerUpdatePayload;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component("MOVE")
public class MoveAction extends Action {
    private final GameEngine gameEngine;
    public MoveAction(GameEngine gameEngine) {
        super("PLAYER_MOVED");
        this.gameEngine = gameEngine;
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final var request = objectMapper.convertValue(payload, MoveRequest.class);
        final String direction = request.getDirection();
        final Player player = gameEngine.processMove(session.getId(), new MoveRequest(direction));
        if (player != null) {
            final PlayerUpdatePayload response = new PlayerUpdatePayload(
                    session.getId(),
                    player.getX(),
                    player.getY()
            );
            return WebSocketResponse.of(type, response);
        }
        return WebSocketResponse.of("ACTION_FAILED", "OBSTACLE");
    }
}
