package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wrapper DTO for all outgoing WebSocket messages.
 *
 * @author Matysekxx
 */
@Getter
@AllArgsConstructor
public class WebSocketResponse {
    /**
     * The type of the response (e.g., MAP_LOAD, PLAYER_MOVED).
     */
    private String type;
    /**
     * The payload of the response.
     */
    private Object payload;

    /**
     * Factory method to create a new WebSocketResponse.
     *
     * @param type    The response type.
     * @param payload The payload object.
     * @return A new WebSocketResponse.
     */
    public static WebSocketResponse of(String type, Object payload) {
        return new WebSocketResponse(type, payload);
    }
}