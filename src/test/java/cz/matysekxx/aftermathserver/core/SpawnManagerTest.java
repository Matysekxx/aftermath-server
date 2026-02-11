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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpawnManagerTest {

    @Mock
    private WorldManager worldManager;
    @Mock
    private MapObjectFactory mapObjectFactory;
    @Mock
    private NpcFactory npcFactory;
    @Mock
    private NpcTable npcTable;
    @Mock
    private ItemTable itemTable;

    private SpawnManager spawnManager;
    private GameMapData testMap;

    @BeforeEach
    void setUp() {
        spawnManager = new SpawnManager(worldManager, mapObjectFactory, npcFactory, npcTable, itemTable);
        
        testMap = new GameMapData();
        testMap.setId("test_map");

        ParsedMapLayer layer = mock(ParsedMapLayer.class);
        when(layer.getWidth()).thenReturn(3);
        when(layer.getHeight()).thenReturn(3);
        when(layer.getTileAt(anyInt(), anyInt())).thenReturn(TileType.FLOOR);
        testMap.setParsedLayers(Map.of(0, layer));

        testMap.getSpawns().put("start", new Vector3(1, 1, 0));
        
        when(worldManager.getMap("test_map")).thenReturn(testMap);
    }

    @Test
    void testSpawnRandomNpcs() {
        NpcTemplate template = new NpcTemplate();
        template.setId("rat");
        when(npcTable.getDefinitions()).thenReturn(List.of(template));
        
        Npc mockNpc = mock(Npc.class);
        when(npcFactory.createNpc(eq("rat"), anyInt(), anyInt(), anyInt(), eq("test_map"))).thenReturn(mockNpc);

        spawnManager.spawnRandomNpcs("test_map", 5);

        assertEquals(5, testMap.getNpcs().size());
        verify(npcFactory, times(5)).createNpc(eq("rat"), anyInt(), anyInt(), anyInt(), eq("test_map"));
    }

    @Test
    void testSpawnRandomLoot() {
        ItemTemplate template = new ItemTemplate();
        template.setId("scrap");
        template.setRarity("COMMON");
        when(itemTable.getDefinitions()).thenReturn(List.of(template));
        
        MapObject mockLoot = new MapObject();
        mockLoot.setId("loot_1"); 

        when(mapObjectFactory.createLootBag(eq("scrap"), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(mockLoot);

        spawnManager.spawnRandomLoot("test_map", 3);

        assertEquals(3, testMap.getObjects().size());
        verify(mapObjectFactory, times(3)).createLootBag(eq("scrap"), anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testSpawnSpecificNpc() {
        NpcTemplate template = new NpcTemplate();
        template.setId("boss");
        when(npcTable.getTemplate("boss")).thenReturn(template);
        
        Npc mockNpc = mock(Npc.class);
        when(npcFactory.createNpc(eq("boss"), anyInt(), anyInt(), anyInt(), eq("test_map"))).thenReturn(mockNpc);

        spawnManager.spawnSpecificNpc("test_map", "boss", 1);

        assertEquals(1, testMap.getNpcs().size());
        verify(npcFactory).createNpc(eq("boss"), anyInt(), anyInt(), anyInt(), eq("test_map"));
    }
}
