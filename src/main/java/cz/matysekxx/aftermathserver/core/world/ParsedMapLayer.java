package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ParsedMapLayer {
    @JsonIgnore
    private final TileType[][] tiles;
    private final List<String> layerData;
    private final int width;
    private final int height;
    
    public ParsedMapLayer(TileType[][] tiles, List<String> layerData) {
        this.tiles = tiles;
        this.layerData = layerData;
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
        List<String> layerData = Arrays.asList(lines);
        int maxWidth = 0;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, line.length());
        }
        
        final TileType[][] tiles = new TileType[lines.length][maxWidth];
        for (int y = 0; y < lines.length; y++) {
            final String line = lines[y];
            boolean inQuotes = false;
            for (int x = 0; x < maxWidth; x++) {
                char c = x < line.length() ? line.charAt(x) : ' ';
                if (c == '"') {
                    inQuotes = !inQuotes;
                    tiles[y][x] = TileType.FLOOR;
                } else {
                    tiles[y][x] = inQuotes ? TileType.FLOOR : registry.getType(c);
                }
            }
        }
        return new ParsedMapLayer(tiles, layerData);
    }
}
