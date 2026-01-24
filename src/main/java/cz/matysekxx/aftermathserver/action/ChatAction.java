package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.ChatRequest;
import org.springframework.stereotype.Component;

/// Handles player chat messages.
///
/// Triggered by the `CHAT` command.
/// Parses the message and delegates it to the GameEngine for broadcasting or command processing.
@Component("CHAT")
public class ChatAction extends Action {

    public ChatAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final var chatData = objectMapper.convertValue(payload, ChatRequest.class);
        gameEngine.handleChatMessage(chatData, sessionId);
    }
}
