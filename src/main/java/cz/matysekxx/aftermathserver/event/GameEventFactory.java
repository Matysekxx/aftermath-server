package cz.matysekxx.aftermathserver.event;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Factory for creating GameEvent instances.
 * <p>
 * Provides static helper methods to create specific types of game events,
 * distinguishing between single-client messages and map-wide broadcasts.
 *
 * @author Matysekxx
 */
public class GameEventFactory {
    private GameEventFactory() {
    }

    /**
     * Creates an event to update a specific player's inventory.
     *
     * @param player The player whose inventory is being sent.
     * @return A new GameEvent.
     */
    public static GameEvent sendInventoryEvent(Player player) {
        return GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), null, false);
    }

    /**
     * Creates an event to update a specific player's statistics (HP, Rads, etc.).
     *
     * @param player The player whose stats are being sent.
     * @return A new GameEvent.
     */
    public static GameEvent sendStatsEvent(Player player) {
        return GameEvent.create(EventType.SEND_STATS, player, player.getId(), null, false);
    }

    /**
     * Creates an event to send map viewport data to a specific player.
     *
     * @param mapViewportPayload The viewport data.
     * @param sessionId          The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendMapDataEvent(MapViewportPayload mapViewportPayload, String sessionId) {
        return GameEvent.create(EventType.SEND_MAP_DATA, mapViewportPayload, sessionId, null, false);
    }

    /**
     * Creates an event to send the "Game Over" screen to a specific player.
     *
     * @param player The player who died.
     * @return A new GameEvent.
     */
    public static GameEvent sendGameOverEvent(Player player) {
        return GameEvent.create(EventType.SEND_GAME_OVER, player, player.getId(), null, false);
    }

    /**
     * Creates an event to update a specific player's position.
     *
     * @param player The player whose position is being sent.
     * @return A new GameEvent.
     */
    public static GameEvent sendPositionEvent(Player player) {
        return GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), null, false);
    }

    /**
     * Creates an event to send a system message to a specific player.
     *
     * @param message   The message content.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendMessageEvent(String message, String sessionId) {
        return GameEvent.create(EventType.SEND_MESSAGE, message, sessionId, null, false);
    }

    /**
     * Creates an event to send an error message to a specific player.
     *
     * @param message   The error message content.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendErrorEvent(String message, String sessionId) {
        return GameEvent.create(EventType.SEND_ERROR, message, sessionId, null, false);
    }

    /**
     * Creates an event to open the Metro UI for a specific player.
     *
     * @param stations  The list of available stations.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendMetroUiEvent(Map.Entry<String, List<MetroStation>> stations, String sessionId) {
        return GameEvent.create(EventType.OPEN_METRO_UI, stations, sessionId, null, false);
    }

    /**
     * Creates an event to open the Trade UI for a specific player.
     *
     * @param tradeOffer The trade offer details.
     * @param sessionId  The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendTradeUiEvent(TradeOfferDto tradeOffer, String sessionId) {
        return GameEvent.create(EventType.OPEN_TRADE_UI, tradeOffer, sessionId, null, false);
    }

    /**
     * Creates an event to send login options (classes, maps) to a specific session.
     *
     * @param response  The login options.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendLoginOptionsEvent(LoginOptionsResponse response, String sessionId) {
        return GameEvent.create(EventType.SEND_LOGIN_OPTIONS, response, sessionId, null, false);
    }


    /**
     * Creates an event to send a list of map objects to a specific player (e.g., on join).
     *
     * @param objects   The collection of map objects.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendMapObjectsToPlayer(Collection<MapObject> objects, String sessionId) {
        return GameEvent.create(EventType.SEND_MAP_OBJECTS, objects, sessionId, null, false);
    }

    /**
     * Creates an event to broadcast a list of map objects to all players on a specific map.
     *
     * @param objects The collection of map objects.
     * @param mapId   The ID of the map.
     * @return A new GameEvent.
     */
    public static GameEvent broadcastMapObjects(Collection<MapObject> objects, String mapId) {
        return GameEvent.create(EventType.SEND_MAP_OBJECTS, objects, null, mapId, true);
    }


    /**
     * Creates an event to send a list of NPCs to a specific player (e.g., on join).
     *
     * @param npcs      The collection of NPCs.
     * @param sessionId The session ID of the recipient.
     * @return A new GameEvent.
     */
    public static GameEvent sendNpcsToPlayer(Collection<NpcDto> npcs, String sessionId) {
        return GameEvent.create(EventType.SEND_NPCS, npcs, sessionId, null, false);
    }

    /**
     * Creates an event to broadcast a list of NPCs to all players on a specific map.
     *
     * @param npcs  The collection of NPCs.
     * @param mapId The ID of the map.
     * @return A new GameEvent.
     */
    public static GameEvent broadcastNpcs(Collection<NpcDto> npcs, String mapId) {
        return GameEvent.create(EventType.SEND_NPCS, npcs, null, mapId, true);
    }


    /**
     * Creates an event to broadcast a chat message to all players on a specific map.
     *
     * @param request The chat request.
     * @param mapId   The ID of the map.
     * @return A new GameEvent.
     */
    public static GameEvent broadcastChatMsgEvent(ChatRequest request, String mapId) {
        return GameEvent.create(EventType.BROADCAST_CHAT_MSG, request, null, mapId, true);
    }

    /**
     * Creates an event to broadcast a global announcement to all players on the server.
     *
     * @param message The announcement message.
     * @return A new GameEvent.
     */
    public static GameEvent sendGlobalAnnouncementEvent(String message) {
        return GameEvent.create(EventType.GLOBAL_ANNOUNCEMENT, message, null, null, true);
    }

    /**
     * Creates an event to broadcast the list of other players on the map.
     *
     * @param players The collection of other players.
     * @param mapId   The ID of the map.
     * @return A new GameEvent.
     */
    public static GameEvent broadcastPlayers(Collection<OtherPlayerDto> players, String mapId) {
        return GameEvent.create(EventType.BROADCAST_PLAYERS, players, null, mapId, true);
    }
}
