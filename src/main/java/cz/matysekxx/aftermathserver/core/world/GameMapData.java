package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

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
}

