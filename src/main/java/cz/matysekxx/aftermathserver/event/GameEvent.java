package cz.matysekxx.aftermathserver.event;

/// Represents an internal event within the game system.
///
/// Carries data between game logic and the network layer.
///
/// @param type            The type of the event.
/// @param payload         The data associated with the event.
/// @param targetSessionId The session ID of the recipient (if unicast).
/// @param mapId           The map ID for broadcasting (if broadcast).
/// @param isBroadcast     Whether this event should be sent to multiple players.
public record GameEvent(EventType type, Object payload, String targetSessionId, String mapId, boolean isBroadcast) {
    /// Factory method to create a new GameEvent.
    public static GameEvent create(EventType type, Object payload, String targetSessionId, String mapId, boolean isBroadcast) {
        return new GameEvent(type, payload, targetSessionId, mapId, isBroadcast);
    }
}
