package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.web.socket.WebSocketSession;


/// Abstract base class for all game actions.
///
/// Represents a single type of action that a client can trigger via WebSocket.
/// Instances are typically Spring components mapped to a specific action string.
public abstract class Action {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected final String type;
    protected final GameEngine gameEngine;

    protected Action(String type, GameEngine gameEngine) {
        this.type = type;
        this.gameEngine = gameEngine;
    }

    /// Executes the action based on the received payload.
    ///
    /// @param session The WebSocket session of the player triggering the action.
    /// @param payload The JSON payload containing action details.
    public abstract void execute(String sessionId, JsonNode payload);
}
