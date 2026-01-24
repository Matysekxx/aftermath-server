package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;

/// Handles the initialization request from the client.
///
/// Triggered by the `INIT` command.
/// Sends back available player classes and spawn locations.
@Component("INIT")
public class InitAction extends Action {
    public InitAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        gameEngine.sendLoginOptions(sessionId);
    }
}