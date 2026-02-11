package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;

/**
 * Handles interactions with map objects and NPCs.
 * Triggered by the "INTERACT" command.
 *
 * @author Matysekxx
 */
@Component("INTERACT")
public class InteractAction extends Action {

    public InteractAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        gameEngine.processInteract(sessionId);
    }
}
