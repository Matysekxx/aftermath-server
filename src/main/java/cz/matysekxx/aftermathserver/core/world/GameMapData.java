package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class GameMapData {
    private String id;
    private String name;
    private MapType type;
    private Environment environment;
    private List<String> layout;
    private Map<String, Coordinate> spawns;
    private List<MapObject> objects;
    private Map<String, String> legend;

    private List<ParsedMapLayer> parsedLayers = new ArrayList<>();
    

    public ParsedMapLayer getLayer(int index) {
        if (index < 0 || index >= parsedLayers.size()) {
            return null;
        }
        return parsedLayers.get(index);
    }

    public int getLayerCount() {
        return parsedLayers.size();
    }
}

