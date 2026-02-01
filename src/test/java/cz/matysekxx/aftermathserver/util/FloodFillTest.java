package cz.matysekxx.aftermathserver.util;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.core.world.TileRegistry;
import cz.matysekxx.aftermathserver.core.world.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FloodFillTest {

    private TileRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TileRegistry();
        TileRegistry.TileDefinition floor = new TileRegistry.TileDefinition();
        floor.setSymbol('.');
        floor.setType(TileType.FLOOR);

        TileRegistry.TileDefinition wall = new TileRegistry.TileDefinition();
        wall.setSymbol('#');
        wall.setType(TileType.WALL);

        TileRegistry.TileDefinition space = new TileRegistry.TileDefinition();
        space.setSymbol(' ');
        space.setType(TileType.FLOOR);

        registry.setDefinitions(List.of(floor, wall, space));
        registry.init();
    }

    @Test
    void testFloodFillStaysInsideWalls() {

        String layout =
                """
                        #####
                        #...#
                        #.@.#
                        #...#
                        #####""";

        GameMapData map = new GameMapData();
        ParsedMapLayer layer = ParsedMapLayer.parse(layout, registry, 0, map);
        map.setParsedLayers(Map.of(0, layer));
        map.setSpawns(Map.of("start", new Vector3(2, 2, 0)));

        List<Vector3> reachable = FloodFill.floodFill(map);

        assertEquals(9, reachable.size(), "Flood fill leaked outside the room!");
        for (Vector3 v : reachable) {
            assertNotEquals(TileType.WALL, layer.getTileAt(v.x(), v.y()));
        }
    }

    @Test
    void testFloodFillDoesNotSpreadToVoid() {
        String layout =
                "###\n" +
                        "#.\n" +
                        "###";

        GameMapData map = new GameMapData();
        ParsedMapLayer layer = ParsedMapLayer.parse(layout, registry, 0, map);
        map.setParsedLayers(Map.of(0, layer));
        map.setSpawns(Map.of("start", new Vector3(1, 1, 0)));

        List<Vector3> reachable = FloodFill.floodFill(map);

        assertEquals(1, reachable.size());
        assertEquals(new Vector3(1, 1, 0), reachable.getFirst());
    }
}