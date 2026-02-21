package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.factory.NpcFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTable;
import cz.matysekxx.aftermathserver.core.model.entity.NpcTemplate;
import cz.matysekxx.aftermathserver.core.model.item.ItemTable;
import cz.matysekxx.aftermathserver.core.model.item.ItemTemplate;
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

/**
 * Service responsible for managing the spawning of objects and entities in the game world.
 * <p>
 * Uses map analysis to ensure objects appear only in locations
 * that are actually accessible to players.
 *
 * @author Matysekxx
 */
@Slf4j
@Service
public class SpawnManager {
    private final WorldManager worldManager;
    private final MapObjectFactory mapObjectFactory;
    private final NpcFactory npcFactory;
    private final NpcTable npcTable;
    private final ItemTable itemTable;
    private final Map<String, List<Vector3>> reachableTilesCache = new ConcurrentHashMap<>();

    /**
     * Constructs the SpawnManager.
     *
     * @param worldManager     The manager for world data.
     * @param mapObjectFactory Factory for creating map objects.
     * @param npcFactory       Factory for creating NPCs.
     * @param npcTable         Configuration table for NPCs.
     * @param itemTable        Configuration table for items.
     */
    public SpawnManager(WorldManager worldManager, MapObjectFactory mapObjectFactory, NpcFactory npcFactory, NpcTable npcTable, ItemTable itemTable) {
        this.worldManager = worldManager;
        this.mapObjectFactory = mapObjectFactory;
        this.npcFactory = npcFactory;
        this.npcTable = npcTable;
        this.itemTable = itemTable;
    }

    /**
     * Retrieves the list of reachable tiles for a specific map.
     * <p>
     * If results for the given map are not in the cache, calculation via Flood Fill is triggered.
     *
     * @param mapId The ID of the map for which we want to get spawnable positions.
     * @return A list of coordinates where it is safe to spawn objects.
     */
    private List<Vector3> getReachableTiles(String mapId) {
        return reachableTilesCache.computeIfAbsent(mapId, s -> floodFill(worldManager.getMap(s)));
    }

    /**
     * Returns the number of reachable tiles on a map.
     * Useful for calculating dynamic entity limits based on map size.
     *
     * @param mapId The ID of the map.
     * @return The count of reachable tiles.
     */
    public int getReachableTileCount(String mapId) {
        return getReachableTiles(mapId).size();
    }

    /**
     * Spawns a specified number of random NPCs on the given map.
     * <p>
     * Selects random reachable tiles to ensure NPCs are not stuck in walls.
     *
     * @param mapId The ID of the target map.
     * @param count The number of NPCs to spawn.
     */
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

    /**
     * Spawns a specified number of random aggressive NPCs on the given map.
     *
     * @param mapId The ID of the target map.
     * @param count The number of NPCs to spawn.
     */
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

    /**
     * Spawns a specific type of NPC on the map.
     *
     * @param mapId         The ID of the target map.
     * @param npcTemplateId The ID of the NPC template (e.g., "mutant_rat").
     * @param count         The number of instances to spawn.
     */
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

    /**
     * Spawns random loot items scattered across the map.
     * <p>
     * Uses the ItemFactory to generate items and places them on valid tiles.
     *
     * @param mapId The ID of the target map.
     * @param count The number of items to spawn.
     */
    public void spawnRandomLoot(String mapId, int count) {
        final List<Vector3> reachableTiles = getReachableTiles(mapId);
        final List<ItemTemplate> allTemplates = itemTable.getDefinitions();
        if (reachableTiles.isEmpty() || allTemplates == null || allTemplates.isEmpty()) return;

        final GameMapData map = worldManager.getMap(mapId);
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            final Vector3 tile = reachableTiles.get(random.nextInt(reachableTiles.size()));
            final ItemTemplate template = selectRandomItemByRarity(allTemplates, random);
            if (template != null) {
                final MapObject lootBag = mapObjectFactory.createLootBag(template.getId(), random.nextInt(1, 3), tile.x(), tile.y(), tile.z());
                map.addObject(lootBag);
            }
        }
    }

    /**
     * Selects a random item template from a list based on weighted rarity chances.
     * <p>
     * The probability distribution is:
     * <ul>
     *     <li>LEGENDARY: 1%</li>
     *     <li>EPIC: 5%</li>
     *     <li>RARE: 15%</li>
     *     <li>UNCOMMON: 30%</li>
     *     <li>COMMON: 49%</li>
     * </ul>
     *
     * @param allTemplates List of all available item templates.
     * @param random       The random number generator to use.
     * @return A randomly selected ItemTemplate, or a fallback if specific rarity lists are empty.
     */
    private ItemTemplate selectRandomItemByRarity(List<ItemTemplate> allTemplates, ThreadLocalRandom random) {
        final double roll = random.nextDouble();

        final List<ItemTemplate> legendary = allTemplates.stream().filter(t -> "LEGENDARY".equalsIgnoreCase(t.getRarity())).toList();
        final List<ItemTemplate> epic = allTemplates.stream().filter(t -> "EPIC".equalsIgnoreCase(t.getRarity())).toList();
        final List<ItemTemplate> rare = allTemplates.stream().filter(t -> "RARE".equalsIgnoreCase(t.getRarity())).toList();
        final List<ItemTemplate> uncommon = allTemplates.stream().filter(t -> "UNCOMMON".equalsIgnoreCase(t.getRarity())).toList();
        final List<ItemTemplate> common = allTemplates.stream().filter(t -> t.getRarity() == null || "COMMON".equalsIgnoreCase(t.getRarity())).toList();

        if (roll < 0.01 && !legendary.isEmpty()) {
            return legendary.get(random.nextInt(legendary.size()));
        } else if (roll < 0.06 && !epic.isEmpty()) {
            return epic.get(random.nextInt(epic.size()));
        } else if (roll < 0.21 && !rare.isEmpty()) {
            return rare.get(random.nextInt(rare.size()));
        } else if (roll < 0.51 && !uncommon.isEmpty()) {
            return uncommon.get(random.nextInt(uncommon.size()));
        } else if (!common.isEmpty()) {
            return common.get(random.nextInt(common.size()));
        }

        return allTemplates.get(random.nextInt(allTemplates.size()));
    }
}