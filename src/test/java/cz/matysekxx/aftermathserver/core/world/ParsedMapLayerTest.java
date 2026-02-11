package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsedMapLayerTest {

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
    void testTrailingSpacesAreConvertedToVoid() {
        String content =
                "#####\n" +
                        "# . #    \n" +
                        "#####";

        ParsedMapLayer layer = ParsedMapLayer.parse(content, registry, 0, null);

        assertEquals(5, layer.getWidth());

        assertEquals(TileType.FLOOR, layer.getTileAt(2, 1));

        assertEquals(TileType.VOID, layer.getTileAt(5, 1));
    }

    @Test
    void testInternalSpacesRemainFloor() {
        String content = "#   #";
        ParsedMapLayer layer = ParsedMapLayer.parse(content, registry, 0, null);

        assertEquals(TileType.FLOOR, layer.getTileAt(1, 0));
        assertEquals(TileType.FLOOR, layer.getTileAt(2, 0));
    }

    @Test
    void testSpawnMarkersAreProcessed() {
        GameMapData mapData = new GameMapData();
        Map<String, String> markers = new HashMap<>();
        markers.put("@", "line_a");
        mapData.setSpawnMarkers(markers);

        String content =
                """
                        ###
                        #@#
                        ###""";

        ParsedMapLayer layer = ParsedMapLayer.parse(content, registry, 0, mapData);

        assertTrue(mapData.getSpawns().containsKey("line_a"));
        assertEquals(new Vector3(1, 1, 0), mapData.getSpawns().get("line_a"));

        assertEquals(' ', layer.getSymbolAt(1, 1));
        assertEquals(TileType.FLOOR, layer.getTileAt(1, 1));
    }

    @Test
    void testOutOfBoundsIsVoid() {
        ParsedMapLayer layer = ParsedMapLayer.parse("###", registry, 0, null);
        assertEquals(TileType.VOID, layer.getTileAt(10, 10));
    }
}