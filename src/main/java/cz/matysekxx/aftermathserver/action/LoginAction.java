package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.LoginRequest;
import org.springframework.stereotype.Component;

@Component("LOGIN")
public class LoginAction extends Action{
    protected LoginAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final var loginRequest = objectMapper.convertValue(payload, LoginRequest.class);
        gameEngine.addPlayer(sessionId, loginRequest);

    }
}
