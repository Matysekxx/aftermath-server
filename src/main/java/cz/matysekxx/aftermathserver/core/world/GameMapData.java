package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.core.world.triggers.Link;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Holds all data regarding a specific game map.
 * Includes layout, objects, triggers, and environment settings.
 *
 * @author Matysekxx
 */
@Data
public class GameMapData {
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    private final Map<String, MapObject> objectCache = new ConcurrentHashMap<>();
    private String id;
    private String name;
    private MapType type;
    private int difficulty;
    private Map<Integer, String> layout = new HashMap<>();
    private Collection<MapObject> objects = new CopyOnWriteArrayList<>();
    private Collection<Npc> npcs = new CopyOnWriteArrayList<>();
    private Map<Integer, ParsedMapLayer> parsedLayers = new HashMap<>();
    private Map<String, TileTrigger> tileTriggers = new HashMap<>();
    @JsonIgnore
    private Map<Vector3, TileTrigger> dynamicTriggers = new HashMap<>();
    private Map<String, String> spawnMarkers = new HashMap<>();
    private Map<String, String> npcMarkers = new HashMap<>();
    private Map<String, ObjectMarker> objectMarkers = new HashMap<>();
    @JsonIgnore
    private Map<String, Vector3> spawns = new ConcurrentHashMap<>();

    private Collection<Link> links;

    /**
     * Retrieves a specific layer by index.
     *
     * @param index The layer index.
     * @return The parsed map layer.
     */
    public ParsedMapLayer getLayer(int index) {
        return parsedLayers.get(index);
    }

    /**
     * Checks if a symbol corresponds to a tile trigger.
     *
     * @param symbol The character symbol.
     * @return An Optional containing the TileTrigger.
     */
    public Optional<TileTrigger> getMaybeTileTrigger(String symbol) {
        if (!tileTriggers.containsKey(symbol)) return Optional.empty();
        return Optional.of(tileTriggers.get(symbol));
    }

    public Optional<TileTrigger> getDynamicTrigger(int x, int y, int z) {
        return Optional.ofNullable(dynamicTriggers.get(new Vector3(x, y, z)));
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

    public void removeObject(MapObject object) {
        if (object == null) return;
        objectCache.remove(object.getId());
        objects.remove(object);
    }

    public void initializeCache() {
        objectCache.clear();
        objects.forEach(obj -> objectCache.put(obj.getId(), obj));
    }

    public Vector3 getMetroSpawn(String lineId) {
        return spawns.get(lineId);
    }

    public boolean isWalkable(int x, int y, int z) {
        final ParsedMapLayer parsedLayer = parsedLayers.get(z);
        if (parsedLayer == null) return false;
        return parsedLayer.getTileAt(x, y).isWalkable();
    }

    @Data
    @NoArgsConstructor
    public static class ObjectMarker {
        private String type;
        private String action;
        private String description;
    }
}
