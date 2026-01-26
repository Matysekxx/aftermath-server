package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
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
        MapViewportPayload payload = new MapViewportPayload();
        payload.setMapName(map.getName());
        payload.setCenterX(px);
        payload.setCenterY(py);
        payload.setRangeX(rx);
        payload.setRangeY(ry);

        final Map<Integer, List<String>> slicedLayers = new HashMap<>();
        for (Integer layerIdx : map.getParsedLayers().keySet()) {
            final List<String> rows = new ArrayList<>();
            for (int y = py - ry; y <= py + ry; y++) {
                final StringBuilder sb = new StringBuilder();
                for (int x = px - rx; x <= px + rx; x++) {
                    sb.append(map.getLayer(layerIdx).getSymbolAt(x, y));
                }
                rows.add(sb.toString());
            }
            slicedLayers.put(layerIdx, rows);
        }
        payload.setLayers(slicedLayers);
        return payload;
    }
}