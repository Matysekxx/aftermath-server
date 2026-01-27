package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.NpcFactory;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTable;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTemplate;
import cz.matysekxx.aftermathserver.core.model.item.ItemFactory;
import cz.matysekxx.aftermathserver.core.model.item.ItemTable;
import cz.matysekxx.aftermathserver.core.model.item.ItemTemplate;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

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
    private final NpcFactory npcFactory;
    private final NpcTable npcTable;
    private final ItemTable itemTable;
    private final Map<String, List<Vector3>> reachableTilesCache = new ConcurrentHashMap<>();


    public SpawnManager(WorldManager worldManager, GameEventQueue gameEventQueue, MapObjectFactory mapObjectFactory, ItemFactory itemFactory, NpcFactory npcFactory, NpcTable npcTable, ItemTable itemTable) {
        this.worldManager = worldManager;
        this.gameEventQueue = gameEventQueue;
        this.mapObjectFactory = mapObjectFactory;
        this.itemFactory = itemFactory;
        this.npcFactory = npcFactory;
        this.npcTable = npcTable;
        this.itemTable = itemTable;
    }

    /// Retrieves the list of reachable tiles for a specific map.
    ///
    /// If results for the given map are not in the cache, calculation via Flood Fill is triggered.
    ///
    /// @param mapId The ID of the map for which we want to get spawnable positions.
    /// @return A list of coordinates where it is safe to spawn objects.
    private List<Vector3> getReachableTiles(String mapId) {
        return reachableTilesCache.computeIfAbsent(mapId, s -> floodFill(worldManager.getMap(s)));
    }

    /// Returns the number of reachable tiles on a map.
    /// Useful for calculating dynamic entity limits based on map size.
    public int getReachableTileCount(String mapId) {
        return getReachableTiles(mapId).size();
    }

    /// Spawns a specified number of random NPCs on the given map.
    ///
    /// Selects random reachable tiles to ensure NPCs are not stuck in walls.
    ///
    /// @param mapId The ID of the target map.
    /// @param count The number of NPCs to spawn.
    public void spawnRandomNpcs(String mapId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final List<NpcTemplate>templates = npcTable.getDefinitions();
        if (reachableTiles.isEmpty() || templates == null || templates.isEmpty()) return;
        final GameMapData mapData = worldManager.getMap(mapId);
        for (int i = 0; i < count; i++) {
            final Vector3 vector3 = reachableTiles.get(ThreadLocalRandom.current().nextInt(reachableTiles.size()));
            final NpcTemplate template = templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
            final Npc npc = npcFactory.createNpc(template.getId(), vector3.x(),  vector3.y(), vector3.z(), mapId);
            mapData.addNpc(npc);
        }
    }

    /// Spawns a specific type of NPC on the map.
    ///
    /// @param mapId The ID of the target map.
    /// @param npcTemplateId The ID of the NPC template (e.g., "mutant_rat").
    /// @param count The number of instances to spawn.
    public void spawnSpecificNpc(String mapId, String npcTemplateId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final GameMapData mapData = worldManager.getMap(mapId);
        if (reachableTiles.isEmpty()) return;
        for (int i = 0; i < count; i++) {
            final Vector3 vector3 = reachableTiles.get(ThreadLocalRandom.current().nextInt(reachableTiles.size()));
            final NpcTemplate template = npcTable.getTemplate(npcTemplateId);
            final Npc npc = npcFactory.createNpc(template.getId(), vector3.x(),  vector3.y(), vector3.z(), mapId);
            mapData.addNpc(npc);
        }
    }

    /// Spawns random loot items scattered across the map.
    ///
    /// Uses the ItemFactory to generate items and places them on valid tiles.
    ///
    /// @param mapId The ID of the target map.
    /// @param count The number of items to spawn.
    public void spawnRandomLoot(String mapId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final List<ItemTemplate> templates = itemTable.getDefinitions();
        if (reachableTiles.isEmpty() || templates == null || templates.isEmpty()) return;

        final GameMapData map = worldManager.getMap(mapId);
        for (int i = 0; i < count; i++) {
            final Vector3 tile = reachableTiles.get(ThreadLocalRandom.current().nextInt(reachableTiles.size()));
            final ItemTemplate template = templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
            final MapObject lootBag = mapObjectFactory.createLootBag(template.getId(), 1, tile.x(), tile.y());
            map.addObject(lootBag);
        }
    }
}
