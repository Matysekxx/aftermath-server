package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.logic.interactions.InteractionLogic;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector2;
import cz.matysekxx.aftermathserver.util.Vector3;
import cz.matysekxx.aftermathserver.util.MathUtil;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/// Service responsible for coordinating interactions between players and map objects.
///
/// It validates the physical possibility of an interaction (distance checks) and 
/// delegates the specific logic to the appropriate [InteractionLogic] implementation.
@Service
public class InteractionService {
    private final Map<String, InteractionLogic> logicMap;
    private final GameEventQueue gameEventQueue;

    /// Constructs the InteractionService.
    ///
    /// @param logicMap A map of interaction keys (e.g., "LOOT", "READ") to their logic handlers.
    /// @param gameEventQueue The queue used to dispatch events resulting from interactions.
    public InteractionService(Map<String, InteractionLogic> logicMap, GameEventQueue gameEventQueue) {
        this.logicMap = logicMap;
        this.gameEventQueue = gameEventQueue;
    }

    /// Processes an interaction between a player and a target object.
    ///
    /// Validates that the target exists and is within range (1 tile). If valid, 
    /// executes the logic associated with the object's action type.
    ///
    /// @param player The player performing the action.
    /// @param target The object being interacted with.
    public void processInteraction(Player player, MapObject target) {
        if (target == null) {
            gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "Object not found", player.getId(), player.getMapId(), false));
            return;
        }

        final int distance = MathUtil.getChebyshevDistance(new Vector2(player.getX(), player.getY()), new Vector2(target.getX(), target.getY()));
        if (distance <= 1) {
            final InteractionLogic interactionLogic = logicMap.get(target.getAction());
            if (interactionLogic != null) {
                final Collection<GameEvent> events = interactionLogic.interact(target, player);
                if (events != null) events.forEach(gameEventQueue::enqueue);
            }
        } else gameEventQueue.enqueue(GameEvent.create(EventType.SEND_ERROR, "You are too far away", player.getId(), player.getMapId(), false));
        
    }
}
