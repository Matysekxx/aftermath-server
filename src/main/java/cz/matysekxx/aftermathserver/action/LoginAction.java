package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.LoginRequest;
import org.springframework.stereotype.Component;

/**
 * Handles player login and character creation.
 * Triggered by the "LOGIN" command.
 *
 * @author Matysekxx
 */
@Component("LOGIN")
public class LoginAction extends Action {

    public LoginAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final LoginRequest request = objectMapper.convertValue(payload, LoginRequest.class);
        gameEngine.addPlayer(sessionId, request);
    }
}