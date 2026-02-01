package cz.matysekxx.aftermathserver.core.world.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.TileRegistry;
import cz.matysekxx.aftermathserver.core.world.TileType;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.Getter;

import java.util.*;

/// Represents a single layer of a parsed map.
///
/// Contains the grid of TileTypes and original symbols.
@Getter
public class ParsedMapLayer {
    private final TileType[][] tiles;
    private final char[][] symbols;
    @JsonIgnore
    private final Map<String, List<Vector3>> markers;
    @JsonIgnore
    private final Map<Vector3, String> npcSpawns;
    @JsonIgnore
    private final Map<Vector3, String> objectSpawns;
    private final int width;
    private final int height;

    private ParsedMapLayer(TileType[][] tiles, char[][] symbols, Map<String, List<Vector3>> markers, Map<Vector3, String> npcSpawns, Map<Vector3, String> objectSpawns) {
        this.tiles = tiles;
        this.symbols = symbols;
        this.height = tiles.length;
        this.width = height > 0 ? tiles[0].length : 0;
        this.markers = markers;
        this.npcSpawns = npcSpawns;
        this.objectSpawns = objectSpawns;
    }

    private static String[] getLinesFromContent(String content) {
        return content.lines()
                .map(String::stripTrailing)
                .toArray(String[]::new);
    }

    /// Parses a string content into a map layer using the registry.
    public static ParsedMapLayer parse(String content, TileRegistry registry, int layerIndex, GameMapData mapData) {
        final String[] lines = getLinesFromContent(content);
        final int height = lines.length;
        final int width = Arrays.stream(lines).mapToInt(String::length).max().orElse(0);

        final ParseContext ctx = new ParseContext(width, height, registry, layerIndex, mapData);

        for (int y = 0; y < height; y++) {
            parseLine(lines[y], y, width, ctx);
        }

        return new ParsedMapLayer(ctx.tiles, ctx.symbols, ctx.markers, ctx.npcSpawns, ctx.objectSpawns);
    }

    private static void parseLine(String line, int y, int width, ParseContext ctx) {
        boolean inQuotes = false;
        for (int x = 0; x < width; x++) {
            if (x < line.length()) {
                char c = line.charAt(x);
                if (processSpecialMarkers(x, y, c, ctx)) {
                    continue;
                }

                ctx.symbols[y][x] = c;
                if (c == '"') inQuotes = !inQuotes;

                processRegularTile(x, y, c, inQuotes, ctx);
            } else {
                ctx.symbols[y][x] = ' ';
                ctx.tiles[y][x] = TileType.VOID;
            }
        }
    }

    private static boolean processSpecialMarkers(int x, int y, char c, ParseContext ctx) {
        if (ctx.mapData == null) return false;
        final String charStr = String.valueOf(c);
        return checkAndSetNpc(x, y, charStr, ctx) || checkAndSetObject(x, y, charStr, ctx) || checkAndSetSpawn(x, y, charStr, ctx);
    }

    private static boolean checkAndSetSpawn(int x, int y, String charStr, ParseContext ctx) {
        if (ctx.mapData.getSpawnMarkers() != null && ctx.mapData.getSpawnMarkers().containsKey(charStr)) {
            final String lineId = ctx.mapData.getSpawnMarkers().get(charStr);
            ctx.mapData.getSpawns().put(lineId, new Vector3(x, y, ctx.layerIndex));
            setFloor(x, y, ctx);
            return true;
        }
        return false;
    }

    private static boolean checkAndSetNpc(int x, int y, String charStr, ParseContext ctx) {
        if (ctx.mapData.getNpcMarkers() != null && ctx.mapData.getNpcMarkers().containsKey(charStr)) {
            final String npcId = ctx.mapData.getNpcMarkers().get(charStr);
            ctx.npcSpawns.put(new Vector3(x, y, ctx.layerIndex), npcId);
            setFloor(x, y, ctx);
            return true;
        }
        return false;
    }

    private static boolean checkAndSetObject(int x, int y, String charStr, ParseContext ctx) {
        if (ctx.mapData.getObjectMarkers() != null && ctx.mapData.getObjectMarkers().containsKey(charStr)) {
            ctx.objectSpawns.put(new Vector3(x, y, ctx.layerIndex), charStr);
            setFloor(x, y, ctx);
            return true;
        }
        return false;
    }

    private static void setFloor(int x, int y, ParseContext ctx) {
        ctx.symbols[y][x] = '.';
        ctx.tiles[y][x] = ctx.registry.getType('.');
    }

    private static void processRegularTile(int x, int y, char c, boolean inQuotes, ParseContext ctx) {
        final TileType type = ctx.registry.getType(c);
        if (!inQuotes && c != '"' && type == TileType.UNKNOWN && c != ' ') {
            ctx.markers.computeIfAbsent(String.valueOf(c), k -> new ArrayList<>())
                    .add(new Vector3(x, y, ctx.layerIndex));
            ctx.tiles[y][x] = ctx.registry.getType('.');
        } else {
            ctx.tiles[y][x] = type;
        }
    }

    /// Gets the tile type at specific coordinates.
    public TileType getTileAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TileType.VOID;
        }
        return tiles[y][x];
    }

    /// Gets the original symbol at specific coordinates.
    public char getSymbolAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ' ';
        }
        return symbols[y][x];
    }

    private static class ParseContext {
        final TileType[][] tiles;
        final char[][] symbols;
        final Map<String, List<Vector3>> markers = new HashMap<>();
        final Map<Vector3, String> npcSpawns = new HashMap<>();
        final Map<Vector3, String> objectSpawns = new HashMap<>();
        final TileRegistry registry;
        final int layerIndex;
        final GameMapData mapData;

        ParseContext(int width, int height, TileRegistry registry, int layerIndex, GameMapData mapData) {
            this.tiles = new TileType[height][width];
            this.symbols = new char[height][width];
            this.registry = registry;
            this.layerIndex = layerIndex;
            this.mapData = mapData;
        }
    }
}