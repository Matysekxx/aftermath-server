package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import org.springframework.stereotype.Component;

/// Handles combat attacks.
///
/// Triggered by the `ATTACK` command.
/// Calculates damage and applies it to the target entity.
@Component("ATTACK")
public class AttackAction extends Action {
    protected AttackAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        gameEngine.processAttack(sessionId);
    }
}
