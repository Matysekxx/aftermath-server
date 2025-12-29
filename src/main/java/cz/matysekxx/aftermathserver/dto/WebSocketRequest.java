package cz.matysekxx.aftermathserver.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WebSocketRequest {
    private String type;
    private JsonNode payload;
}