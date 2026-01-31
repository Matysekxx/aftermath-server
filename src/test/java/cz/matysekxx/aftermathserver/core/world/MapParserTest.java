package cz.matysekxx.aftermathserver.core.world;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapParserTest {

    private TileRegistry registry;
    private MapParser parser;

    @BeforeEach
    void setUp() {
        registry = new TileRegistry();
        Map<Character, TileType> testMapping = new HashMap<>();
        testMapping.put('#', TileType.WALL);
        testMapping.put('.', TileType.FLOOR);
        testMapping.put(' ', TileType.EMPTY);
        testMapping.put('=', TileType.DOOR);
        testMapping.put('B', TileType.BED);
        testMapping.put('V', TileType.ELEVATOR);
        registry.setMapping(testMapping);
        parser = new MapParser(registry, null, null);
    }

    @Test
    void shouldMapDefaultChars() {
        assertEquals(TileType.WALL, registry.getType('#'));
        assertEquals(TileType.FLOOR, registry.getType('.'));
        assertEquals(TileType.EMPTY, registry.getType(' '));
        assertEquals(TileType.DOOR, registry.getType('='));
        assertEquals(TileType.BED, registry.getType('B'));
    }

    @Test
    void shouldReturnUnknown() {
        assertEquals(TileType.UNKNOWN, registry.getType('?'));
        assertEquals(TileType.UNKNOWN, registry.getType('X'));
    }

    @Test
    void shouldParseAsciiMap() {
        String map = "####\n#..#\n####";
        ParsedMapLayer layer = parser.parseString(map);

        assertEquals(4, layer.getWidth());
        assertEquals(3, layer.getHeight());
    }

    @Test
    void shouldIdentifyTiles() {
        String map = "#.=\nBV#";
        ParsedMapLayer layer = parser.parseString(map);

        assertEquals(TileType.WALL, layer.getTileAt(0, 0));
        assertEquals(TileType.FLOOR, layer.getTileAt(1, 0));
        assertEquals(TileType.DOOR, layer.getTileAt(2, 0));
        assertEquals(TileType.BED, layer.getTileAt(0, 1));
        assertEquals(TileType.ELEVATOR, layer.getTileAt(1, 1));
    }

    @Test
    void shouldReturnVoidOutOfBounds() {
        ParsedMapLayer layer = parser.parseString("###");

        assertEquals(TileType.VOID, layer.getTileAt(-1, 0));
        assertEquals(TileType.VOID, layer.getTileAt(100, 0));
    }

    @Test
    void shouldDetectWalkability() {
        assertTrue(TileType.FLOOR.isWalkable());
        assertTrue(TileType.DOOR.isWalkable());
        assertFalse(TileType.WALL.isWalkable());
        assertFalse(TileType.BED.isWalkable());
    }
}
