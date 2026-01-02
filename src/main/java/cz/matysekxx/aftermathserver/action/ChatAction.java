package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component("CHAT")
public class ChatAction extends Action {
    public ChatAction() {
        super("CHAT_MSG");
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final var chatData = objectMapper.convertValue(payload, ChatRequest.class);
        return WebSocketResponse.of(type,chatData);
    }
}
