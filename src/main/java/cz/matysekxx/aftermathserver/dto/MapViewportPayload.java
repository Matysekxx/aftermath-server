package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.ParsedMapLayer;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// DTO for sending a sliced portion of the map centered around the player.
@Data
public class MapViewportPayload {
    private String mapName;
    private int centerX;
    private int centerY;
    private int rangeX;
    private int rangeY;
    private Map<Integer, List<String>> layers;

    public static MapViewportPayload of(GameMapData map, int px, int py, int rx, int ry) {
        final MapViewportPayload payload = new MapViewportPayload();
        payload.setMapName(map.getName());
        payload.setCenterX(px);
        payload.setCenterY(py);
        payload.setRangeX(rx);
        payload.setRangeY(ry);

        final Map<Integer, List<String>> slicedLayers = new HashMap<>();
        for (Map.Entry<Integer, ParsedMapLayer> entry : map.getParsedLayers().entrySet()) {
            final List<String> rows = new ArrayList<>();
            final ParsedMapLayer layer = entry.getValue();
            
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
            slicedLayers.put(entry.getKey(), rows);
        }
        payload.setLayers(slicedLayers);
        return payload;
    }
}