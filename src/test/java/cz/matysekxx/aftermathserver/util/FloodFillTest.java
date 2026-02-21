package cz.matysekxx.aftermathserver.util;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.TileType;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import org.junit.jupiter.api.Test;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FloodFillTest {

    @NonNull
    private static ParsedMapLayer getParsedMapLayer() {
        final TileType[][] tiles = {
                {TileType.FLOOR, TileType.FLOOR, TileType.WALL},
                {TileType.FLOOR, TileType.FLOOR, TileType.WALL},
                {TileType.WALL, TileType.WALL, TileType.WALL}
        };
        final char[][] symbols = {
                {'.', '.', '#'},
                {'.', '.', '#'},
                {'#', '#', '#'}
        };

        return new ParsedMapLayer(tiles, symbols, new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    @Test
    void testFloodFillSimpleRoom() {
        final ParsedMapLayer layer = getParsedMapLayer();
        final GameMapData mapData = new GameMapData();

        final Map<Integer, ParsedMapLayer> layers = new HashMap<>();
        layers.put(0, layer);
        mapData.setParsedLayers(layers);

        final Map<String, Vector3> spawns = new HashMap<>();
        spawns.put("default", Vector3.of(0, 0, 0));
        mapData.setSpawns(spawns);

        final List<Vector3> reachable = FloodFill.floodFill(mapData);

        assertEquals(4, reachable.size(), "Should find exactly 4 reachable tiles");

        assertTrue(reachable.contains(Vector3.of(0, 0, 0)));
        assertTrue(reachable.contains(Vector3.of(1, 0, 0)));
        assertTrue(reachable.contains(Vector3.of(0, 1, 0)));
        assertTrue(reachable.contains(Vector3.of(1, 1, 0)));

        assertFalse(reachable.contains(Vector3.of(2, 0, 0)), "Wall at (2,0) should not be reachable");
        assertFalse(reachable.contains(Vector3.of(2, 2, 0)), "Wall at (2,2) should not be reachable");
    }

    @Test
    void testFloodFillNullMap() {
        final List<Vector3> result = FloodFill.floodFill(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFloodFillNoSpawns() {
        final GameMapData mapData = new GameMapData();
        mapData.setSpawns(new HashMap<>());

        final List<Vector3> result = FloodFill.floodFill(mapData);
        assertTrue(result.isEmpty(), "Reachable tiles should be empty if there are no spawns");
    }
}