package cz.matysekxx.aftermathserver.event;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.ChatRequest;
import cz.matysekxx.aftermathserver.dto.LoginOptionsResponse;
import cz.matysekxx.aftermathserver.dto.MapViewportPayload;
import cz.matysekxx.aftermathserver.dto.NpcDto;
import cz.matysekxx.aftermathserver.dto.TradeOfferDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/// Factory for creating GameEvent instances.
///
/// Provides static helper methods to create specific types of game events,
/// distinguishing between single-client messages and map-wide broadcasts.
public class GameEventFactory {
    private GameEventFactory() {
    }

    /// Creates an event to update a specific player's inventory.
    public static GameEvent sendInventoryEvent(Player player) {
        return GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), null, false);
    }

    /// Creates an event to update a specific player's statistics (HP, Rads, etc.).
    public static GameEvent sendStatsEvent(Player player) {
        return GameEvent.create(EventType.SEND_STATS, player, player.getId(), null, false);
    }

    /// Creates an event to send map viewport data to a specific player.
    public static GameEvent sendMapDataEvent(MapViewportPayload mapViewportPayload, String sessionId) {
        return GameEvent.create(EventType.SEND_MAP_DATA, mapViewportPayload, sessionId, null, false);
    }

    /// Creates an event to send the "Game Over" screen to a specific player.
    public static GameEvent sendGameOverEvent(Player player) {
        return GameEvent.create(EventType.SEND_GAME_OVER, player, player.getId(), null, false);
    }

    /// Creates an event to update a specific player's position.
    public static GameEvent sendPositionEvent(Player player) {
        return GameEvent.create(EventType.SEND_PLAYER_POSITION, player, player.getId(), null, false);
    }

    /// Creates an event to send a system message to a specific player.
    public static GameEvent sendMessageEvent(String message, String sessionId) {
        return GameEvent.create(EventType.SEND_MESSAGE, message, sessionId, null, false);
    }

    /// Creates an event to send an error message to a specific player.
    public static GameEvent sendErrorEvent(String message, String sessionId) {
        return GameEvent.create(EventType.SEND_ERROR, message, sessionId, null, false);
    }

    /// Creates an event to open the Metro UI for a specific player.
    public static GameEvent sendMetroUiEvent(Map.Entry<String, List<MetroStation>> stations, String sessionId) {
        return GameEvent.create(EventType.OPEN_METRO_UI, stations, sessionId, null, false);
    }

    /// Creates an event to open the Trade UI for a specific player.
    public static GameEvent sendTradeUiEvent(TradeOfferDto tradeOffer, String sessionId) {
        return GameEvent.create(EventType.OPEN_TRADE_UI, tradeOffer, sessionId, null, false);
    }

    /// Creates an event to send login options (classes, maps) to a specific session.
    public static GameEvent sendLoginOptionsEvent(LoginOptionsResponse response, String sessionId) {
        return GameEvent.create(EventType.SEND_LOGIN_OPTIONS, response, sessionId, null, false);
    }


    /// Creates an event to send a list of map objects to a specific player (e.g., on join).
    public static GameEvent sendMapObjectsToPlayer(Collection<MapObject> objects, String sessionId) {
        return GameEvent.create(EventType.SEND_MAP_OBJECTS, objects, sessionId, null, false);
    }

    /// Creates an event to broadcast a list of map objects to all players on a specific map.
    public static GameEvent broadcastMapObjects(Collection<MapObject> objects, String mapId) {
        return GameEvent.create(EventType.SEND_MAP_OBJECTS, objects, null, mapId, true);
    }


    /// Creates an event to send a list of NPCs to a specific player (e.g., on join).
    public static GameEvent sendNpcsToPlayer(Collection<NpcDto> npcs, String sessionId) {
        return GameEvent.create(EventType.SEND_NPCS, npcs, sessionId, null, false);
    }

    /// Creates an event to broadcast a list of NPCs to all players on a specific map.
    public static GameEvent broadcastNpcs(Collection<NpcDto> npcs, String mapId) {
        return GameEvent.create(EventType.SEND_NPCS, npcs, null, mapId, true);
    }


    /// Creates an event to broadcast a chat message to all players on a specific map.
    public static GameEvent broadcastChatMsgEvent(ChatRequest request, String mapId) {
        return GameEvent.create(EventType.BROADCAST_CHAT_MSG, request, null, mapId, true);
    }
}
