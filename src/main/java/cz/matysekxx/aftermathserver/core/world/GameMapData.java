package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.util.Coordination;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class GameMapData {
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private final Map<String, MapObject> objectCache = new ConcurrentHashMap<>();
    private String id;
    private String name;
    private MapType type;
    private Map<String, Coordination> spawns = new HashMap<>();
    private Environment environment;
    private List<String> layout = new ArrayList<>();
    private List<MapObject> objects = new CopyOnWriteArrayList<>();
    private List<ParsedMapLayer> parsedLayers = new ArrayList<>();
    private Map<String, TileTrigger> tileTriggers = new HashMap<>();

    public ParsedMapLayer getLayer(int index) {
        if (index < 0 || index >= parsedLayers.size()) {
            return null;
        }
        return parsedLayers.get(index);
    }

    public Optional<TileTrigger> getMaybeTileTrigger(String symbol) {
        if (!tileTriggers.containsKey(symbol)) return Optional.empty();
        return Optional.of(tileTriggers.get(symbol));
    }

    public int getLayerCount() {
        return parsedLayers.size();
    }

    public void addObject(MapObject object) {
        objects.add(object);
        objectCache.put(object.getId(), object);
    }

    public MapObject getObject(String id) {
        return objectCache.get(id);
    }

    public void initializeCache() {
        objectCache.clear();
        objects.forEach(obj -> objectCache.put(obj.getId(), obj));
    }

    public Coordination getMetroSpawn(String lineId) {
        return spawns.get(lineId);
    }
}
