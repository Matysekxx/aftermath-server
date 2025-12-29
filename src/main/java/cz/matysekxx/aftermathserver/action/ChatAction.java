package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.dto.GameDtos;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.web.socket.WebSocketSession;

public class ChatAction extends Action {
    public ChatAction() {
        super("CHAT_MSG");
    }

    @Override
    public WebSocketResponse execute(WebSocketSession session, JsonNode payload) {
        final var chatData = objectMapper.convertValue(payload, GameDtos.ChatReq.class);
        return WebSocketResponse.of(type,chatData);
    }
}
