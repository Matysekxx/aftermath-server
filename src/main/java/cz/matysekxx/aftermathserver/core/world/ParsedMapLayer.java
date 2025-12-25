package cz.matysekxx.aftermathserver.core.world;

import lombok.Getter;

@Getter
public class ParsedMapLayer {
    private final TileType[][] tiles;
    private final int width;
    private final int height;
    
    public ParsedMapLayer(TileType[][] tiles) {
        this.tiles = tiles;
        this.height = tiles.length;
        this.width = height > 0 ? tiles[0].length : 0;
    }

    public TileType getTileAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return TileType.VOID;
        }
        return tiles[y][x];
    }

    public static ParsedMapLayer parse(String content, TileRegistry registry) {
        String[] lines = content.split("\r?\n");
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, line.length());
        }
        
        TileType[][] tiles = new TileType[lines.length][maxWidth];
        for (int y = 0; y < lines.length; y++) {
            String line = lines[y];
            for (int x = 0; x < maxWidth; x++) {
                char c = x < line.length() ? line.charAt(x) : ' ';
                tiles[y][x] = registry.getType(c);
            }
        }
        return new ParsedMapLayer(tiles);
    }
}
