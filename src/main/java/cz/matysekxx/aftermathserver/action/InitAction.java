package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Handles the initial connection request from the client.
 * Triggered by the "INIT" command to receive login options.
 *
 * @author Matysekxx
 */
@Slf4j
@Component("INIT")
public class InitAction extends Action {

    public InitAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        log.info("Executing INIT action for session: {}", sessionId);
        gameEngine.sendLoginOptions(sessionId);
    }
}