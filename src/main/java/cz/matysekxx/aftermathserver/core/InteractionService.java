package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.logic.interactions.npc.NpcInteractionLogic;
import cz.matysekxx.aftermathserver.core.logic.interactions.object.ObjectInteractionLogic;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Spatial;
import cz.matysekxx.aftermathserver.util.Vector2;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service responsible for coordinating interactions between players, map objects, and NPCs.
 * <p>
 * It validates the physical possibility of an interaction (distance checks) and
 * delegates the specific logic to the appropriate interaction logic implementations.
 *
 * @author Matysekxx
 */
@Service
public class InteractionService {
    private final Map<String, ObjectInteractionLogic> objectInteractionLogicMap;
    private final GameEventQueue gameEventQueue;
    private final Map<InteractionType, NpcInteractionLogic> npcInteractionLogicMap = new EnumMap<>(InteractionType.class);
    private final WorldManager worldManager;

    /**
     * Constructs the InteractionService.
     *
     * @param objectInteractionLogicMap A map of interaction keys (e.g., "LOOT", "READ") to their logic handlers.
     * @param gameEventQueue            The queue used to dispatch events resulting from interactions.
     * @param npcInteractionLogicList   List of NPC interaction logic handlers.
     * @param worldManager              The manager for world data.
     */
    public InteractionService(Map<String, ObjectInteractionLogic> objectInteractionLogicMap, GameEventQueue gameEventQueue, List<NpcInteractionLogic> npcInteractionLogicList, WorldManager worldManager) {
        this.objectInteractionLogicMap = objectInteractionLogicMap;
        this.gameEventQueue = gameEventQueue;
        this.worldManager = worldManager;
        npcInteractionLogicList.forEach(npcInteractionLogic ->
                npcInteractionLogicMap.put(npcInteractionLogic.getType(), npcInteractionLogic));
    }

    /**
     * Processes an interaction between a player and a target object.
     * <p>
     * Validates that the target exists and is within range (1 tile). If valid,
     * executes the logic associated with the object's action type.
     *
     * @param player The player performing the action.
     * @param target The object being interacted with.
     */
    public void processObjectInteraction(Player player, MapObject target) {
        final ObjectInteractionLogic objectInteractionLogic = objectInteractionLogicMap.get(target.getAction());
        if (objectInteractionLogic != null) {
            final Collection<GameEvent> events = objectInteractionLogic.interact(target, player);
            if (events != null) events.forEach(gameEventQueue::enqueue);
        }
    }

    /**
     * Processes an interaction between a player and an NPC.
     * <p>
     * Delegates the interaction logic based on the NPC's interaction type.
     *
     * @param player The player initiating the interaction.
     * @param npc    The NPC being interacted with.
     */
    public void processNpcInteraction(Player player, Npc npc) {
        final InteractionType type = npc.getInteraction();
        if (npcInteractionLogicMap.containsKey(type)) {
            final NpcInteractionLogic logic = npcInteractionLogicMap.get(type);
            final Collection<GameEvent> events = logic.interact(npc, player);
            if (events != null) events.forEach(gameEventQueue::enqueue);
        }
    }

    /**
     * Processes a generic interaction request from a player.
     * <p>
     * Automatically finds the nearest valid target (Object or NPC) within range
     * and executes the appropriate interaction logic.
     *
     * @param player The player initiating the interaction.
     */
    public void processInteraction(Player player) {
        final var maybeMap = worldManager.getMaybeMap(player.getMapId());
        if (maybeMap.isEmpty()) return;
        final GameMapData map = maybeMap.get();
        final List<Spatial> candidates = new ArrayList<>();
        map.getObjects().stream()
                .filter(o -> o.getLayerIndex() == player.getLayerIndex())
                .forEach(candidates::add);
        map.getNpcs().stream()
                .filter(n -> n.getLayerIndex() == player.getLayerIndex() && !n.isDead())
                .forEach(candidates::add);

        final Spatial target = candidates.stream()
                .filter(n -> (n instanceof MapObject || (n instanceof Npc npc && !npc.isDead())))
                .filter(n -> MathUtil.getChebyshevDistance(
                        Vector2.of(player.getX(), player.getY()),
                        Vector2.of(n.getX(), n.getY())) <= 2)
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
