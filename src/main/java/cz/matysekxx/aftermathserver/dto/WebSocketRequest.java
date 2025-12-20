package cz.matysekxx.aftermathserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
@NoArgsConstructor
public class WebSocketRequest {
    private String type;
    private JsonNode payload;
}