package cz.matysekxx.aftermathserver.event;

public record GameEvent(EventType type, Object payload, String targetSessionId, boolean isBroadcast) {
    public static GameEvent create(EventType type, Object payload, String targetSessionId, boolean isBroadcast) {
        return new GameEvent(type, payload, targetSessionId, isBroadcast);
    }
}
