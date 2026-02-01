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
import cz.matysekxx.aftermathserver.util.Spatial;
import cz.matysekxx.aftermathserver.util.Vector2;
import org.springframework.stereotype.Service;

import java.util.*;

/// Service responsible for coordinating interactions between players and map objects.
///
/// It validates the physical possibility of an interaction (distance checks) and
/// delegates the specific logic to the appropriate [ObjectInteractionLogic] implementation.
@Service
public class InteractionService {
    private final Map<String, ObjectInteractionLogic> objectInteractionLogicMap;
    private final GameEventQueue gameEventQueue;
    private final Map<InteractionType, NpcInteractionLogic> npcInteractionLogicMap = new EnumMap<>(InteractionType.class);

    private final SpatialService spatialService;

    /// Constructs the InteractionService.
    ///
    /// @param objectInteractionLogicMap A map of interaction keys (e.g., "LOOT", "READ") to their logic handlers.
    /// @param gameEventQueue            The queue used to dispatch events resulting from interactions.
    public InteractionService(Map<String, ObjectInteractionLogic> objectInteractionLogicMap, GameEventQueue gameEventQueue, List<NpcInteractionLogic> npcInteractionLogicList, SpatialService spatialService) {
        this.objectInteractionLogicMap = objectInteractionLogicMap;
        this.gameEventQueue = gameEventQueue;
        this.spatialService = spatialService;
        npcInteractionLogicList.forEach(npcInteractionLogic -> npcInteractionLogicMap.put(npcInteractionLogic.getType(), npcInteractionLogic));
    }

    /// Processes an interaction between a player and a target object.
    ///
    /// Validates that the target exists and is within range (1 tile). If valid,
    /// executes the logic associated with the object's action type.
    ///
    /// @param player The player performing the action.
    /// @param target The object being interacted with.
    public void processObjectInteraction(Player player, MapObject target) {
        final ObjectInteractionLogic objectInteractionLogic = objectInteractionLogicMap.get(target.getAction());
        if (objectInteractionLogic != null) {
            final Collection<GameEvent> events = objectInteractionLogic.interact(target, player);
            if (events != null) events.forEach(gameEventQueue::enqueue);
        }
    }

    public void processNpcInteraction(Player player, Npc npc) {
        final InteractionType type = npc.getInteraction();
        if (npcInteractionLogicMap.containsKey(type)) {
            final NpcInteractionLogic logic = npcInteractionLogicMap.get(type);
            final Collection<GameEvent> events = logic.interact(npc, player);
            if (events != null) events.forEach(gameEventQueue::enqueue);
        }
    }

    public void processInteraction(Player player) {
        final Spatial target = spatialService.getNearby(player.getMapId(), player)
                .stream().filter(n -> !(n instanceof Player))
                .filter(n -> MathUtil.getChebyshevDistance(
                        Vector2.of(player.getX(), player.getY()),
                        Vector2.of(n.getX(), n.getY())
                ) <= 2)
                .min(Comparator.comparingInt(n -> MathUtil.getChebyshevDistance(
                        Vector2.of(player.getX(), player.getY()),
                        Vector2.of(n.getX(), n.getY())
                ))).orElse(null);
        switch (target) {
            case MapObject mapObject -> processObjectInteraction(player, mapObject);
            case Npc npc -> processNpcInteraction(player, npc);
            case null, default ->
                    gameEventQueue.enqueue(GameEventFactory.sendErrorEvent("Target not found", player.getId()));
        }
    }
}
