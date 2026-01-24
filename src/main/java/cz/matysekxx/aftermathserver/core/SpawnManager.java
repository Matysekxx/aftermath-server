package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.ItemFactory;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Coordination;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SpawnManager {
    private final WorldManager worldManager;
    private final GameEventQueue gameEventQueue;
    private final MapObjectFactory mapObjectFactory;
    private final ItemFactory itemFactory;
    private final Map<String, List<Coordination>> reachableTilesCache = new ConcurrentHashMap<>();


    public SpawnManager(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, ItemFactory itemFactory) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.itemFactory = itemFactory;
    }

    private List<Coordination> getReachableTiles(String mapId) {
        return reachableTilesCache.computeIfAbsent(mapId, s -> floodFill(worldManager.getMap(s)));
    }

    private List<Coordination> floodFill(GameMapData gameMapData) {
        final Collection<Coordination> starts = gameMapData.getSpawns().values();
        for (Coordination c : starts) {
            floodMap(gameMapData, c);
        }
        return reachableTilesCache.get(gameMapData.getId());
    }

    private void floodMap(GameMapData gameMapData, Coordination start) {
        //TODO:  pridat implementaci flood fill
    }

    private boolean isWalkable(GameMapData map, Coordination c) {
        return isWalkable(map, c.x(), c.y(), c.z());
    }

    private boolean isWalkable(GameMapData map, int x, int y, int z) {
        if (z < 0 || z >= map.getLayerCount()) return false;
        return map.getLayer(z).getTileAt(x, y).isWalkable();
    }
}
