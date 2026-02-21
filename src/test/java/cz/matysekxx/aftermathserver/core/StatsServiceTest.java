package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatsServiceTest {

    private FakeWorldManager worldManager;
    private GameEventQueue gameEventQueue;
    private StatsService statsService;
    private Player player;
    private GameMapData hazardMap;
    private GameMapData safeMap;

    @BeforeEach
    void setUp() {
        worldManager = new FakeWorldManager();
        gameEventQueue = new GameEventQueue();
        statsService = new StatsService(worldManager, gameEventQueue, Collections.emptyMap());

        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        config.setRadsLimit(20);

        player = new Player("session1", "TestPlayer", new Vector3(0, 0, 0), config, "prosek", "scavenger");

        hazardMap = new GameMapData();
        hazardMap.setId("prosek");
        hazardMap.setType(MapType.HAZARD_ZONE);
        hazardMap.setDifficulty(3);

        safeMap = new GameMapData();
        safeMap.setId("prosek");
        safeMap.setType(MapType.SAFE_ZONE);
    }

    @Test
    void testApplyRadiationWithoutMask() {
        worldManager.addMap(hazardMap);

        final boolean changed = statsService.applyStats(player);

        assertTrue(changed);
        assertEquals(1, player.getRads());
    }

    @Test
    void testApplyRadiationDamageWhenLimitExceeded() {
        worldManager.addMap(hazardMap);
        player.setRads(25);

        final boolean changed = statsService.applyStats(player);

        assertTrue(changed);
        assertEquals(97, player.getHp());
    }

    @Test
    void testMaskDurabilityDecreases() {
        worldManager.addMap(hazardMap);

        final Item mask = Item.builder()
                .id("filter")
                .type(ItemType.MASK)
                .durability(10)
                .healAmount(50)
                .quantity(1)
                .maxStack(1)
                .weight(0.1)
                .build();

        player.getInventory().addItem(mask);
        player.setEquippedMaskSlot(0);

        statsService.applyStats(player);

        final Item equippedMask = player.getInventory().getSlots().get(0);
        assertEquals(9, equippedMask.getDurability());
    }

    @Test
    void testRegenerationInSafeZone() {
        worldManager.addMap(safeMap);
        player.setHp(50);
        player.setRads(10);

        final boolean changed = statsService.applyStats(player);

        assertTrue(changed);
        assertEquals(51, player.getHp());
    }

    @Test
    void testRadiationReductionInSafeZone() {
        worldManager.addMap(safeMap);
        player.setHp(100);
        player.setRads(10);

        final boolean changed = statsService.applyStats(player);

        assertTrue(changed);
        assertEquals(5, player.getRads());
    }

    private static class FakeWorldManager extends WorldManager {
        private final Map<String, GameMapData> maps = new HashMap<>();

        public FakeWorldManager() {
            super(null);
        }

        public void addMap(GameMapData map) {
            maps.put(map.getId(), map);
        }

        @Override
        public GameMapData getMap(String id) {
            return maps.get(id);
        }
    }
}