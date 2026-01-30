package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private final int width;
    private final int height;

    private ParsedMapLayer(TileType[][] tiles, char[][] symbols, Map<String, List<Vector3>> markers) {
        this.tiles = tiles;
        this.symbols = symbols;
        this.height = tiles.length;
        this.width = height > 0 ? tiles[0].length : 0;
        this.markers = markers;
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

        final TileType[][] tiles = new TileType[height][width];
        final char[][] symbols = new char[height][width];
        final Map<String, List<Vector3>> markers = new HashMap<>();

        for (int y = 0; y < height; y++) {
            final String line = lines[y];
            boolean inQuotes = false;
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    final char c = line.charAt(x);
                    final String charStr = String.valueOf(c);
                    if (mapData != null && mapData.getSpawnMarkers() != null &&
                            mapData.getSpawnMarkers().containsKey(charStr)) {
                        final String lineId = mapData.getSpawnMarkers().get(charStr);
                        mapData.getSpawns().put(lineId, new Vector3(x, y, layerIndex));
                        symbols[y][x] = '.';
                        tiles[y][x] = registry.getType('.');
                    } else {
                        symbols[y][x] = c;
                        if (c == '"') {
                            inQuotes = !inQuotes;
                        }
                        final TileType type = registry.getType(c);
                        if (!inQuotes && c != '"' && type == TileType.UNKNOWN && c != ' ') {
                            markers.computeIfAbsent(charStr, k -> new ArrayList<>())
                                    .add(new Vector3(x, y, layerIndex));
                            tiles[y][x] = registry.getType('.');
                        } else {
                            tiles[y][x] = type;
                        }
                    }
                } else {
                    symbols[y][x] = ' ';
                    tiles[y][x] = TileType.VOID;
                }
            }
        }
        return new ParsedMapLayer(tiles, symbols, markers);
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
}