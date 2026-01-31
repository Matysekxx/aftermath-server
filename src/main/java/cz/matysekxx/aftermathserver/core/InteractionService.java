package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.logic.interactions.npc.NpcInteractionLogic;
import cz.matysekxx.aftermathserver.core.logic.interactions.object.ObjectInteractionLogic;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Vector2;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/// Service responsible for coordinating interactions between players and map objects.
///
/// It validates the physical possibility of an interaction (distance checks) and
/// delegates the specific logic to the appropriate [ObjectInteractionLogic] implementation.
@Service
public class InteractionService {
    private final Map<String, ObjectInteractionLogic> objectInteractionLogicMap;
    private final GameEventQueue gameEventQueue;
    private final Map<InteractionType, NpcInteractionLogic> npcInteractionLogicMap = new EnumMap<>(InteractionType.class);

    /// Constructs the InteractionService.
    ///
    /// @param objectInteractionLogicMap       A map of interaction keys (e.g., "LOOT", "READ") to their logic handlers.
    /// @param gameEventQueue The queue used to dispatch events resulting from interactions.
    public InteractionService(Map<String, ObjectInteractionLogic> objectInteractionLogicMap, GameEventQueue gameEventQueue, List<NpcInteractionLogic> npcInteractionLogicList) {
        this.objectInteractionLogicMap = objectInteractionLogicMap;
        this.gameEventQueue = gameEventQueue;
        npcInteractionLogicList.forEach(npcInteractionLogic -> npcInteractionLogicMap.put(npcInteractionLogic.getType(), npcInteractionLogic));
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
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Object not found", player.getId()));
            return;
        }

        final int distance = MathUtil.getChebyshevDistance(new Vector2(player.getX(), player.getY()), new Vector2(target.getX(), target.getY()));
        if (distance <= 1) {
            final ObjectInteractionLogic objectInteractionLogic = objectInteractionLogicMap.get(target.getAction());
            if (objectInteractionLogic != null) {
                final Collection<GameEvent> events = objectInteractionLogic.interact(target, player);
                if (events != null) events.forEach(gameEventQueue::enqueue);
            }
        } else gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You are too far away", player.getId()));

    }

    public void processNpcInteraction(Player player, Npc npc) {
        final int distance = MathUtil.getChebyshevDistance(
          Vector2.of(player.getX(), player.getY()),
          Vector2.of(npc.getX(), npc.getY())
        );
        if (distance > 2) {
            gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("You are too far away", player.getId()));
            return;
        }

        final InteractionType type = npc.getInteraction();
        if (npcInteractionLogicMap.containsKey(type)) {
            final NpcInteractionLogic logic = npcInteractionLogicMap.get(type);
            final Collection<GameEvent> events = logic.interact(npc, player);
            if (events != null) events.forEach(gameEventQueue::enqueue);
        }
    }
}
