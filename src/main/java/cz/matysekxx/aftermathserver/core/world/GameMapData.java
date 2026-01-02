package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class GameMapData {
    private String id;
    private String name;
    private MapType type;
    private Environment environment;
    private List<String> layout = new ArrayList<>();

    private List<MapObject> objects = new CopyOnWriteArrayList<>();

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

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private final Map<String, MapObject> objectCache = new ConcurrentHashMap<>();

    public void addObject(MapObject object) {
        objects.add(object);
        objectCache.put(object.getId(), object);
    }

    public MapObject getObject(String id) {
        return objectCache.get(id);
    }
}
