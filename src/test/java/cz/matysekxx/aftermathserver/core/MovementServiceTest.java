package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.TileType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MovementServiceTest {

    private FakeWorldManager worldManager;
    private FakeGameEventQueue gameEventQueue;
    private MovementService movementService;

    @BeforeEach
    void setUp() {
        worldManager = new FakeWorldManager();
        gameEventQueue = new FakeGameEventQueue();
        movementService = new MovementService(worldManager, gameEventQueue, null);
    }

    @Test
    void testMovementProcess_Obstacle() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        config.setRadsLimit(20);
        Player player = new Player("session-1", "User", new Vector3(0, 0, 0), config, "map1", "SOLDIER");

        final TileType[][] tiles = {{TileType.FLOOR, TileType.WALL}};
        final char[][] symbols = {{'.', '#'}};
        final ParsedMapLayer layer = new ParsedMapLayer(tiles, symbols, new HashMap<>(), new HashMap<>(), new HashMap<>());
        final GameMapData mapData = new GameMapData();
        mapData.setId("map1");
        mapData.setParsedLayers(Map.of(0, layer));
        worldManager.addMap(mapData);

        final MoveRequest request = new MoveRequest("RIGHT");

        movementService.movementProcess(player, request);

        assertEquals(0, player.getX());
        assertNotNull(gameEventQueue.lastEvent);
        assertEquals("SEND_ERROR", gameEventQueue.lastEvent.type().name());
    }

    @Test
    void testMovementProcess_ValidMove() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        config.setRadsLimit(20);
        final Player player = new Player("session-1", "User", new Vector3(0, 0, 0), config, "map1", "SOLDIER");

        final TileType[][] tiles = {{TileType.FLOOR, TileType.FLOOR}};
        final char[][] symbols = {{'.', '.'}};
        final ParsedMapLayer layer = new ParsedMapLayer(tiles, symbols, new HashMap<>(), new HashMap<>(), new HashMap<>());
        final GameMapData mapData = new GameMapData();
        mapData.setId("map1");
        mapData.setParsedLayers(Map.of(0, layer));
        worldManager.addMap(mapData);

        final MoveRequest request = new MoveRequest("RIGHT");

        movementService.movementProcess(player, request);

        assertEquals(1, player.getX());
    }

    private static class FakeWorldManager extends WorldManager {
        private final Map<String, GameMapData> maps = new HashMap<>();
        public FakeWorldManager() { super(null); }
        public void addMap(GameMapData map) { maps.put(map.getId(), map); }
        @Override public GameMapData getMap(String id) { return maps.get(id); }
        @Override public boolean isWalkable(String id, int l, int x, int y) {
            GameMapData m = getMap(id);
            return m != null && m.getLayer(l).getTileAt(x, y).isWalkable();
        }
    }

    private static class FakeGameEventQueue extends GameEventQueue {
        GameEvent lastEvent;
        @Override public void enqueue(GameEvent event) { lastEvent = event; }
    }
}