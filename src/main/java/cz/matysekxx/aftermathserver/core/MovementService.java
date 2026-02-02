package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TriggerContext;
import cz.matysekxx.aftermathserver.dto.MapViewportPayload;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Direction;
import cz.matysekxx.aftermathserver.util.Vector2;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/// Service responsible for handling player movement logic.
///
/// It validates movement requests, checks for collisions with obstacles,
/// updates player positions, and triggers tile-based events (like teleports or metro entry).
@Service
public class MovementService {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MetroService metroService;

    /// Constructs the MovementService.
    ///
    /// @param worldManager   The world manager for map data.
    /// @param gameEventQueue The event queue for sending updates.
    /// @param metroService   The service for metro travel logic.
    public MovementService(WorldManager worldManager, GameEventQueue gameEventQueue, MetroService metroService) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.metroService = metroService;
    }

    /// Processes a movement request for a player.
    ///
    /// Calculates the target position, checks for walkability, updates the player's
    /// coordinates, and executes any triggers present on the destination tile.
    ///
    /// @param player      The player attempting to move.
    /// @param moveRequest The DTO containing the movement direction.
    public void movementProcess(Player player, MoveRequest moveRequest) {
        int targetX = player.getX();
        int targetY = player.getY();

        final var dir = Direction.valueOf(moveRequest.getDirection().toUpperCase());
        targetX += dir.getDx();
        targetY += dir.getDy();

        final Vector2 target = new Vector2(targetX, targetY);

        if (!canMoveTo(player, target)) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("OBSTACLE", player.getId()));
            return;
        }

        player.setX(target.x());
        player.setY(target.y());
        final GameMapData currentMap = worldManager.getMap(player.getMapId());
        final TriggerContext triggerContext = new TriggerContext(metroService);
        currentMap.getDynamicTrigger(target.x(), target.y(), player.getLayerIndex())
                .ifPresentOrElse(
                        tileTrigger -> tileTrigger.onEnter(player, triggerContext),
                        () -> currentMap.getMaybeTileTrigger(
                                String.valueOf(currentMap.getLayer(player.getLayerIndex()).getSymbolAt(target)))
                                .ifPresent(tileTrigger -> tileTrigger.onEnter(player, triggerContext)));

        gameEventQueue.enqueue(GameEventFactory.sendPositionEvent(player));

        final var viewport = MapViewportPayload.of(
                currentMap, player.getX(), player.getY(), GameEngine.VIEWPORT_RANGE_X, GameEngine.VIEWPORT_RANGE_Y
        );
        gameEventQueue.enqueue(GameEventFactory.sendMapDataEvent(viewport, player.getId()));
    }

    /// Checks if a player can move to target coordinates.
    public boolean canMoveTo(Player player, Vector2 target) {
        return worldManager.isWalkable(
                player.getMapId(),
                player.getLayerIndex(),
                target.x(),
                target.y()
        );
    }
}
