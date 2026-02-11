package cz.matysekxx.aftermathserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Wrapper DTO for all incoming WebSocket messages.
 *
 * @author Matysekxx
 */
@Getter
@Setter
@NoArgsConstructor
public class WebSocketRequest {
    /** The type of the request (e.g., MOVE, CHAT, TRAVEL). */
    private String type;
    /** The payload of the request as a JSON node. */
    private JsonNode payload;
}