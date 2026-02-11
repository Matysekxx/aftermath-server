package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import org.springframework.stereotype.Component;

/**
 * Handles the HELP command.
 *
 * @author Matysekxx
 */
@Component("HELP")
public class HelpAction extends Action {
    private final GameEventQueue gameEventQueue;
    public HelpAction(GameEngine gameEngine, GameEventQueue gameEventQueue) {
        super(gameEngine);
        this.gameEventQueue = gameEventQueue;
    }
    @Override
    public void execute(String sessionId, JsonNode payload) {
        gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("Commands: MOVE, ATTACK, USE, EQUIP, DROP, BUY, SELL, TRAVEL, CHAT", sessionId));
    }
}