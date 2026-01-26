package cz.matysekxx.aftermathserver.core.world;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

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

        ParsedMapLayer layer = ParsedMapLayer.parse(content, registry, 0);

        assertEquals(5, layer.getWidth());

        assertEquals(TileType.FLOOR, layer.getTileAt(2, 1));

        assertEquals(TileType.VOID, layer.getTileAt(5, 1));
    }

    @Test
    void testInternalSpacesRemainFloor() {
        String content = "#   #";
        ParsedMapLayer layer = ParsedMapLayer.parse(content, registry, 0);

        assertEquals(TileType.FLOOR, layer.getTileAt(1, 0));
        assertEquals(TileType.FLOOR, layer.getTileAt(2, 0));
    }

    @Test
    void testOutOfBoundsIsVoid() {
        ParsedMapLayer layer = ParsedMapLayer.parse("###", registry, 0);
        assertEquals(TileType.VOID, layer.getTileAt(10, 10));
    }
}