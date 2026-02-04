package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.factory.NpcFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTable;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTemplate;
import cz.matysekxx.aftermathserver.core.model.item.ItemTable;
import cz.matysekxx.aftermathserver.core.model.item.ItemTemplate;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static cz.matysekxx.aftermathserver.util.FloodFill.floodFill;

/// Service responsible for managing the spawning of objects and entities in the game world.
///
/// Uses map analysis to ensure objects appear only in locations
/// that are actually accessible to players.
@Slf4j
@Service
public class SpawnManager {
    private final WorldManager worldManager;
    private final MapObjectFactory mapObjectFactory;
    private final NpcFactory npcFactory;
    private final NpcTable npcTable;
    private final ItemTable itemTable;
    private final Map<String, List<Vector3>> reachableTilesCache = new ConcurrentHashMap<>();

    /// Constructs the SpawnManager.
    ///
    /// @param worldManager     The manager for world data.
    /// @param mapObjectFactory Factory for creating map objects.
    /// @param npcFactory       Factory for creating NPCs.
    /// @param npcTable         Configuration table for NPCs.
    /// @param itemTable        Configuration table for items.
    public SpawnManager(WorldManager worldManager, MapObjectFactory mapObjectFactory, NpcFactory npcFactory, NpcTable npcTable, ItemTable itemTable) {
        this.worldManager = worldManager;
        this.mapObjectFactory = mapObjectFactory;
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
        final List<NpcTemplate> templates = npcTable.getDefinitions();
        if (reachableTiles.isEmpty() || templates == null || templates.isEmpty()) return;
        final GameMapData mapData = worldManager.getMap(mapId);
        spawnNpcs(mapId, count, reachableTiles, templates, mapData);
    }

    private void spawnNpcs(String mapId, int count, List<Vector3> reachableTiles, List<NpcTemplate> templates, GameMapData mapData) {
        for (int i = 0; i < count; i++) {
            final Vector3 vector3 = reachableTiles.get(ThreadLocalRandom.current().nextInt(reachableTiles.size()));
            final NpcTemplate template = templates.get(ThreadLocalRandom.current().nextInt(templates.size()));
            final Npc npc = npcFactory.createNpc(template.getId(), vector3.x(), vector3.y(), vector3.z(), mapId);
            mapData.addNpc(npc);
        }
    }

    /// Spawns a specified number of random aggressive NPCs on the given map.
    ///
    /// @param mapId The ID of the target map.
    /// @param count The number of NPCs to spawn.
    public void spawnRandomAggressiveNpcs(String mapId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final List<NpcTemplate> templates = npcTable.getDefinitions().stream().filter(NpcTemplate::isAggressive).toList();
        if (reachableTiles.isEmpty() || templates.isEmpty()) return;
        final GameMapData mapData = worldManager.getMap(mapId);
        spawnNpcs(mapId, count, reachableTiles, templates, mapData);
    }

    public void spawnRandomTraderNpcs(String mapId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final List<NpcTemplate> templates = npcTable.getTraderNpcs();
        if (reachableTiles.isEmpty() || templates.isEmpty()) {
            return;
        }
        final GameMapData mapData = worldManager.getMap(mapId);
        spawnNpcs(mapId, count, reachableTiles, templates, mapData);
    }

    /// Spawns a specific type of NPC on the map.
    ///
    /// @param mapId         The ID of the target map.
    /// @param npcTemplateId The ID of the NPC template (e.g., "mutant_rat").
    /// @param count         The number of instances to spawn.
    public void spawnSpecificNpc(String mapId, String npcTemplateId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final GameMapData mapData = worldManager.getMap(mapId);
        if (reachableTiles.isEmpty()) return;
        for (int i = 0; i < count; i++) {
            final Vector3 vector3 = reachableTiles.get(ThreadLocalRandom.current().nextInt(reachableTiles.size()));
            final NpcTemplate template = npcTable.getTemplate(npcTemplateId);
            final Npc npc = npcFactory.createNpc(template.getId(), vector3.x(), vector3.y(), vector3.z(), mapId);
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
        final List<ItemTemplate> allTemplates = itemTable.getDefinitions();
        if (reachableTiles.isEmpty() || allTemplates == null || allTemplates.isEmpty()) return;

        final GameMapData map = worldManager.getMap(mapId);
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            final Vector3 tile = reachableTiles.get(random.nextInt(reachableTiles.size()));
            final ItemTemplate template = selectRandomItemByRarity(allTemplates, random);
            final MapObject lootBag = mapObjectFactory.createLootBag(template.getId(), random.nextInt(1, 3), tile.x(), tile.y(), tile.z());
            map.addObject(lootBag);
        }
    }

    private ItemTemplate selectRandomItemByRarity(List<ItemTemplate> allTemplates, ThreadLocalRandom random) {
        final double randomDouble = random.nextDouble();
        if (randomDouble < 0.05) {
            final List<ItemTemplate> items = allTemplates.stream().filter(t -> t.getType() == ItemType.VALUABLE).toList();
            if (!items.isEmpty()) return items.get(random.nextInt(items.size()));
        } 
        
        if (randomDouble < 0.15) {
            final List<ItemTemplate> items = allTemplates.stream().filter(t -> t.getType() == ItemType.WEAPON).toList();
            if (!items.isEmpty()) return items.get(random.nextInt(items.size()));
        }

        final List<ItemTemplate> common = allTemplates.stream()
                .filter(t -> t.getType() == ItemType.RESOURCE || t.getType() == ItemType.CONSUMABLE)
                .toList();
        
        if (common.isEmpty()) return allTemplates.get(random.nextInt(allTemplates.size()));
        return common.get(random.nextInt(common.size()));
    }
}
