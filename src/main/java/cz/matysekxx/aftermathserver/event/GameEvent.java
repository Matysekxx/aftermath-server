package cz.matysekxx.aftermathserver.event;

public record GameEvent(EventType type, Object payload, String targetSessionId, String mapId, boolean isBroadcast) {
    public static GameEvent create(EventType type, Object payload, String targetSessionId, String mapId, boolean isBroadcast) {
        return new GameEvent(type, payload, targetSessionId, mapId, isBroadcast);
    }
}
