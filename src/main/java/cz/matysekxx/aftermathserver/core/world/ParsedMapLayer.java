package cz.matysekxx.aftermathserver.core.world;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class ParsedMapLayer {
    private final TileType[][] tiles;
    private final char[][] symbols;
    private final int width;
    private final int height;
    
    public ParsedMapLayer(TileType[][] tiles, char[][] symbols) {
        this.tiles = tiles;
        this.symbols = symbols;
        this.height = tiles.length;
        this.width = height > 0 ? tiles[0].length : 0;
    }

    public TileType getTileAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TileType.VOID;
        }
        return tiles[y][x];
    }

    public char getSymbolAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return ' ';
        }
        return symbols[y][x];
    }

    public static ParsedMapLayer parse(String content, TileRegistry registry) {
        final String[] lines = content.split("\\R");
        final int height = lines.length;
        final int width = Arrays.stream(lines).mapToInt(String::length).max().orElse(0);
        
        final TileType[][] tiles = new TileType[height][width];
        final char[][] symbols = new char[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines[y];
            boolean inQuotes = false;

            for (int x = 0; x < width; x++) {
                final char c = x < line.length() ? line.charAt(x) : ' ';
                symbols[y][x] = c;

                if (c == '"') {
                    inQuotes = !inQuotes;
                    tiles[y][x] = TileType.FLOOR;
                    continue;
                }
                tiles[y][x] = inQuotes ? TileType.FLOOR : registry.getType(c);
            }
        }
        return new ParsedMapLayer(tiles, symbols);
    }
}
