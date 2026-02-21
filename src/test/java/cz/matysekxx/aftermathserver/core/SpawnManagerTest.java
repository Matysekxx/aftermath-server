package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.factory.NpcFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTable;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTemplate;
import cz.matysekxx.aftermathserver.core.model.item.ItemTable;
import cz.matysekxx.aftermathserver.core.model.item.ItemTemplate;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.TileType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpawnManagerTest {

    private FakeWorldManager worldManager;
    private FakeMapObjectFactory mapObjectFactory;
    private FakeNpcFactory npcFactory;
    private FakeNpcTable npcTable;
    private FakeItemTable itemTable;

    private SpawnManager spawnManager;
    private GameMapData testMap;

    @BeforeEach
    void setUp() {
        worldManager = new FakeWorldManager();
        mapObjectFactory = new FakeMapObjectFactory();
        npcFactory = new FakeNpcFactory();
        npcTable = new FakeNpcTable();
        itemTable = new FakeItemTable();

        spawnManager = new SpawnManager(worldManager, mapObjectFactory, npcFactory, npcTable, itemTable);

        testMap = new GameMapData();
        testMap.setId("test_map");

        final ParsedMapLayer layer = new FakeParsedMapLayer(3, 3, TileType.FLOOR);
        testMap.setParsedLayers(Map.of(0, layer));

        testMap.getSpawns().put("start", new Vector3(1, 1, 0));

        worldManager.addMap(testMap);
    }

    @Test
    void testSpawnRandomNpcs() {
        final NpcTemplate template = new NpcTemplate();
        template.setId("rat");
        npcTable.setDefinitions(List.of(template));

        spawnManager.spawnRandomNpcs("test_map", 5);

        assertEquals(5, testMap.getNpcs().size());
        assertEquals(5, npcFactory.createNpcCallCount);
        assertEquals("rat", npcFactory.lastCreatedId);
    }

    @Test
    void testSpawnRandomLoot() {
        final ItemTemplate template = new ItemTemplate();
        template.setId("scrap");
        template.setRarity("COMMON");
        itemTable.setDefinitions(List.of(template));

        spawnManager.spawnRandomLoot("test_map", 3);

        assertEquals(3, testMap.getObjects().size());
        assertEquals(3, mapObjectFactory.createLootBagCallCount);
        assertEquals("scrap", mapObjectFactory.lastCreatedId);
    }

    @Test
    void testSpawnSpecificNpc() {
        final NpcTemplate template = new NpcTemplate();
        template.setId("boss");
        npcTable.addTemplate("boss", template);

        spawnManager.spawnSpecificNpc("test_map", "boss", 1);

        assertEquals(1, testMap.getNpcs().size());
        assertEquals(1, npcFactory.createNpcCallCount);
        assertEquals("boss", npcFactory.lastCreatedId);
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
        public GameMapData getMap(String id) {
            return maps.get(id);
        }
    }

    private static class FakeMapObjectFactory extends MapObjectFactory {
        int createLootBagCallCount = 0;
        String lastCreatedId;

        public FakeMapObjectFactory() {
            super(null);
        }

        @Override
        public MapObject createLootBag(String itemId, int x, int y, int z, int amount) {
            createLootBagCallCount++;
            lastCreatedId = itemId;
            MapObject obj = new MapObject();
            obj.setId("loot_" + createLootBagCallCount);
            return obj;
        }
    }

    private static class FakeNpcFactory extends NpcFactory {
        int createNpcCallCount = 0;
        String lastCreatedId;

        public FakeNpcFactory() {
            super(null, null, null);
        }

        @Override
        public Npc createNpc(String id, int x, int y, int z, String mapId) {
            createNpcCallCount++;
            lastCreatedId = id;
            return new Npc(id + "-" + createNpcCallCount, id, x, y, z, mapId, 100, null, null);
        }
    }

    private static class FakeNpcTable extends NpcTable {
        private final Map<String, NpcTemplate> templates = new HashMap<>();
        private List<NpcTemplate> definitions = new ArrayList<>();

        public FakeNpcTable() {
            super();
        }

        public void addTemplate(String id, NpcTemplate template) {
            templates.put(id, template);
        }

        @Override
        public List<NpcTemplate> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(List<NpcTemplate> definitions) {
            this.definitions = definitions;
        }

        @Override
        public NpcTemplate getTemplate(String id) {
            return templates.get(id);
        }
    }

    private static class FakeItemTable extends ItemTable {
        private List<ItemTemplate> definitions = new ArrayList<>();

        public FakeItemTable() {
            super();
        }

        @Override
        public List<ItemTemplate> getDefinitions() {
            return definitions;
        }

        public void setDefinitions(List<ItemTemplate> definitions) {
            this.definitions = definitions;
        }
    }

    private static class FakeParsedMapLayer extends ParsedMapLayer {
        private final int width;
        private final int height;
        private final TileType defaultTile;

        public FakeParsedMapLayer(int width, int height, TileType defaultTile) {
            super(new TileType[][]{{TileType.FLOOR}}, new char[][]{{'.'}}, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
            this.width = width;
            this.height = height;
            this.defaultTile = defaultTile;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public TileType getTileAt(int x, int y) {
            return defaultTile;
        }
    }
}