package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.logic.interactions.npc.NpcInteractionLogic;
import cz.matysekxx.aftermathserver.core.logic.interactions.object.ObjectInteractionLogic;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InteractionServiceTest {

    private InteractionService interactionService;
    private FakeWorldManager worldManager;
    private FakeGameEventQueue gameEventQueue;
    private Map<String, ObjectInteractionLogic> objectLogics;
    private List<NpcInteractionLogic> npcLogics;

    @BeforeEach
    void setUp() {
        worldManager = new FakeWorldManager();
        gameEventQueue = new FakeGameEventQueue();
        objectLogics = new HashMap<>();
        npcLogics = new ArrayList<>();

        objectLogics.put("LOOT", new FakeObjectLogic());
        npcLogics.add(new FakeNpcLogic(InteractionType.TALK));

        interactionService = new InteractionService(objectLogics, gameEventQueue, npcLogics, worldManager);
    }

    @Test
    void testProcessObjectInteraction() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player("p1", "Player", Vector3.of(0, 0, 0), config, "map1", "SOLDIER");
        final MapObject obj = new MapObject();
        obj.setAction("LOOT");

        interactionService.processObjectInteraction(player, obj);

        assertNotNull(gameEventQueue.lastEvent);
    }

    @Test
    void testProcessNpcInteraction() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player("p1", "Player", Vector3.of(0, 0, 0), config, "map1", "SOLDIER");
        final Npc npc = new Npc("n1", "Npc", 0, 0, 0, "map1", 100, null, InteractionType.TALK);

        interactionService.processNpcInteraction(player, npc);

        assertNotNull(gameEventQueue.lastEvent);
    }

    @Test
    void testProcessInteraction_FindsNearest() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        final Player player = new Player("p1", "Player", Vector3.of(10, 10, 0), config, "map1", "SOLDIER");
        final GameMapData map = new GameMapData();
        map.setId("map1");

        final MapObject farObj = new MapObject();
        farObj.setId("far_obj");
        farObj.setX(20);
        farObj.setY(20);
        farObj.setZ(0);
        farObj.setAction("LOOT");
        map.addObject(farObj);

        final Npc nearNpc = new Npc("n1", "Npc", 11, 10, 0, "map1", 100, null, InteractionType.TALK);
        map.addNpc(nearNpc);

        worldManager.addMap(map);

        interactionService.processInteraction(player);

        assertNotNull(gameEventQueue.lastEvent);
    }

    private static class FakeWorldManager extends WorldManager {
        private final Map<String, GameMapData> maps = new HashMap<>();

        public FakeWorldManager() {
            super(null);
        }

        public void addMap(GameMapData map) {
            maps.put(map.getId(), map);
        }

        @Override
        public Optional<GameMapData> getMaybeMap(String id) {
            return Optional.ofNullable(maps.get(id));
        }
    }

    private static class FakeGameEventQueue extends GameEventQueue {
        GameEvent lastEvent;

        @Override
        public void enqueue(GameEvent event) {
            lastEvent = event;
        }
    }

    private static class FakeObjectLogic implements ObjectInteractionLogic {
        @Override
        public Collection<GameEvent> interact(MapObject target, Player player) {
            return List.of(GameEventFactory.sendMessageEvent("Object interaction", player.getId()));
        }
    }

    private static class FakeNpcLogic implements NpcInteractionLogic {
        private final InteractionType type;

        public FakeNpcLogic(InteractionType type) {
            this.type = type;
        }

        @Override
        public Collection<GameEvent> interact(Npc target, Player player) {
            return List.of(GameEventFactory.sendMessageEvent("NPC interaction", player.getId()));
        }

        @Override
        public InteractionType getType() {
            return type;
        }
    }
}