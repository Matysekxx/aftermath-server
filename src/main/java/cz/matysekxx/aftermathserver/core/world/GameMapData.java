package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
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
    private Map<String, Coordinate> spawns = new HashMap<>();

    private List<MapObject> objects = new CopyOnWriteArrayList<>();

    private Map<String, String> legend = new HashMap<>();

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

    public void setObjects(List<MapObject> objects) {
        if (objects != null) {
            this.objects = new CopyOnWriteArrayList<>(objects);
            this.objectCache.clear();
            for (MapObject obj : objects) {
                this.objectCache.put(obj.getId(), obj);
            }
        } else {
            this.objects = new CopyOnWriteArrayList<>();
            this.objectCache.clear();
        }
    }

    public void addObject(MapObject object) {
        objects.add(object);
        objectCache.put(object.getId(), object);
    }

    public MapObject getObject(String id) {
        return objectCache.get(id);
    }
}
