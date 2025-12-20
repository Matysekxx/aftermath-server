package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WebSocketResponse {
    private String type;
    private Object payload;

    public static WebSocketResponse of(String type, Object payload) {
        return new WebSocketResponse(type, payload);
    }
}