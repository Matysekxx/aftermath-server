package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.world.triggers.Link;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.util.Coordination;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/// Holds all data regarding a specific game map.
///
/// Includes layout, objects, triggers, and environment settings.
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
    private Map<Integer, String> layout = new HashMap<>();
    private Collection<MapObject> objects = new CopyOnWriteArrayList<>();
    private Collection<Npc> npcs = new CopyOnWriteArrayList<>();
    private Map<Integer, ParsedMapLayer> parsedLayers = new HashMap<>();
    private Map<String, TileTrigger> tileTriggers = new HashMap<>();
    @JsonIgnore
    private Map<Coordination, TileTrigger> dynamicTriggers = new HashMap<>();

    private Collection<Link> links;

    /// Retrieves a specific layer by index.
    public ParsedMapLayer getLayer(int index) {
        return parsedLayers.get(index);
    }

    /// Checks if a symbol corresponds to a tile trigger.
    public Optional<TileTrigger> getMaybeTileTrigger(String symbol) {
        if (!tileTriggers.containsKey(symbol)) return Optional.empty();
        return Optional.of(tileTriggers.get(symbol));
    }

    public Optional<TileTrigger> getDynamicTrigger(int x, int y, int z) {
        return Optional.ofNullable(dynamicTriggers.get(new Coordination(x, y, z)));
    }

    public int getLayerCount() {
        return parsedLayers.size();
    }

    public void addObject(MapObject object) {
        objects.add(object);
        objectCache.put(object.getId(), object);
    }

    public void addNpc(Npc npc) {
        npcs.add(npc);
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
