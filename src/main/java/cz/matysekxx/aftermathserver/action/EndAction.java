package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;

/**
 * Handles the END command to disconnect.
 *
 * @author Matysekxx
 */
@Component("END")
public class EndAction extends Action {
    public EndAction(GameEngine gameEngine) {
        super(gameEngine);
    }
    @Override
    public void execute(String sessionId, JsonNode payload) {
        gameEngine.removePlayer(sessionId);
    }
}