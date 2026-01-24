package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.ItemFactory;
import cz.matysekxx.aftermathserver.core.world.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Coordination;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static cz.matysekxx.aftermathserver.util.FloodFill.floodFill;

/// Service responsible for managing the spawning of objects and entities in the game world.
///
/// Uses map analysis to ensure objects appear only in locations
/// that are actually accessible to players.
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

    /// Retrieves the list of reachable tiles for a specific map.
    ///
    /// If results for the given map are not in the cache, calculation via Flood Fill is triggered.
    ///
    /// @param mapId The ID of the map for which we want to get spawnable positions.
    /// @return A list of coordinates where it is safe to spawn objects.
    private List<Coordination> getReachableTiles(String mapId) {
        return reachableTilesCache.computeIfAbsent(mapId, s -> floodFill(worldManager.getMap(s)));
    }
}
