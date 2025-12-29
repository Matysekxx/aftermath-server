package cz.matysekxx.aftermathserver.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class GameEvent {
    private final String type;
    private final Object payload;
    private final String targetSessionId;
    private final boolean isBroadcast;
}
