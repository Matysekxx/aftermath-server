package cz.matysekxx.aftermathserver.event;

/**
 * Enumeration of all possible game event types.
 * <p>
 * Used to categorize events flowing through the {@link GameEventQueue}.
 *
 * @author Matysekxx
 */
public enum EventType {
    /**
     * Send inventory update to client.
     */
    SEND_INVENTORY,
    /**
     * Send player statistics update to client.
     */
    SEND_STATS,
    /**
     * Send map layout and metadata to client.
     */
    SEND_MAP_DATA,
    /**
     * Send list of interactive objects on the map.
     */
    SEND_MAP_OBJECTS,
    /**
     * Send game over notification.
     */
    SEND_GAME_OVER,
    /**
     * Send player position update.
     */
    SEND_PLAYER_POSITION,
    /**
     * Send a generic text message to client.
     */
    SEND_MESSAGE,
    /**
     * Send an error message to client.
     */
    SEND_ERROR,
    /**
     * Broadcast a chat message to players on the same map.
     */
    BROADCAST_CHAT_MSG,
    /**
     * Trigger opening of the Metro travel UI on client.
     */
    OPEN_METRO_UI,
    /**
     * Send available login options (classes, maps) to client.
     */
    SEND_LOGIN_OPTIONS,
    /**
     * Send list of NPCs to client.
     */
    SEND_NPCS,
    /**
     * Open trade UI on client.
     */
    OPEN_TRADE_UI,
    /**
     * Broadcast a message to all players on the server.
     */
    GLOBAL_ANNOUNCEMENT,
    /**
     * Broadcast positions of other players on the map.
     */
    BROADCAST_PLAYERS,
    /**
     * Request to pay debt.
     */
    PAY_DEBT;
}
