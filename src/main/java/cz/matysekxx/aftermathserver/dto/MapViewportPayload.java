package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO for sending a sliced portion of the map centered around the player.
 *
 * @author Matysekxx
 */
@Data
public class MapViewportPayload {
    private String mapName;
    private int centerX;
    private int centerY;
    private int centerZ;
    private int rangeX;
    private int rangeY;
    private Map<Integer, List<String>> layers;

    /**
     * Creates a viewport payload from the map data and player position.
     *
     * @param map The game map data.
     * @param px  Player X coordinate.
     * @param py  Player Y coordinate.
     * @param pz  Player Z coordinate (layer).
     * @param rx  Viewport range X.
     * @param ry  Viewport range Y.
     * @return The constructed payload.
     */
    public static MapViewportPayload of(GameMapData map, int px, int py, int pz, int rx, int ry) {
        final MapViewportPayload payload = new MapViewportPayload();
        payload.setMapName(map.getName());
        payload.setCenterX(px);
        payload.setCenterY(py);
        payload.setCenterZ(pz);
        payload.setRangeX(rx);
        payload.setRangeY(ry);

        final Map<Integer, List<String>> slicedLayers = new HashMap<>();
        final ParsedMapLayer layer = map.getLayer(pz);
        if (layer != null) {
            final List<String> rows = new ArrayList<>();

            for (int y = py - ry; y <= py + ry; y++) {
                final StringBuilder sb = new StringBuilder();
                for (int x = px - rx; x <= px + rx; x++) {
                    if (x < 0 || y < 0 || x >= layer.getWidth() || y >= layer.getHeight()) {
                        sb.append(' ');
                    } else {
                        sb.append(layer.getSymbolAt(x, y));
                    }
                }
                rows.add(sb.toString());
            }
            slicedLayers.put(pz, rows);
        }
        payload.setLayers(slicedLayers);
        return payload;
    }
}