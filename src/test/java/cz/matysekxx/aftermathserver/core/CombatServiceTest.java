package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CombatServiceTest {

    private FakeWorldManager worldManager;
    private FakeGameEventQueue gameEventQueue;
    private CombatService combatService;

    private Player player;
    private Npc npc;
    private GameMapData map;

    @BeforeEach
    void setUp() {
        worldManager = new FakeWorldManager();
        gameEventQueue = new FakeGameEventQueue();
        combatService = new CombatService(worldManager, gameEventQueue, null);
    }

    @Test
    void testHandleAttack_NoWeaponEquipped() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        player = new Player("player-1", "Hero", new Vector3(0,0,0), config, "letnany", "SOLDIER");
        player.setEquippedWeaponSlot(null);

        combatService.handleAttack(player);

        assertNotNull(gameEventQueue.lastEvent);
        assertEquals("SEND_ERROR", gameEventQueue.lastEvent.type().name());
    }

    @Test
    void testHandleAttack_SuccessfulHit() {
        final PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        player = new Player("player-1", "Hero", new Vector3(0,0,0), config, "letnany", "SOLDIER");

        final Item weapon = Item.builder()
                .type(ItemType.WEAPON)
                .damage(10)
                .range(2)
                .cooldown(100)
                .quantity(1)
                .maxStack(1)
                .weight(1.0)
                .id("weapon")
                .build();

        player.getInventory().addItem(weapon);
        player.setEquippedWeaponSlot(0);
        player.setLastAttackTime(0L);

        npc = new Npc("npc-1", "Mutant", 1, 1, 0, "letnany", 100, null, null);
        map = new GameMapData();
        map.setId("letnany");
        map.addNpc(npc);

        worldManager.addMap(map);

        combatService.handleAttack(player);

        assertEquals(90, npc.getHp());
        assertTrue(player.getLastAttackTime() > 0);
    }

    private static class FakeWorldManager extends WorldManager {
        private final Map<String, GameMapData> maps = new HashMap<>();
        public FakeWorldManager() { super(null); }
        public void addMap(GameMapData map) { maps.put(map.getId(), map); }
        @Override public GameMapData getMap(String id) { return maps.get(id); }
    }

    private static class FakeGameEventQueue extends GameEventQueue {
        GameEvent lastEvent;
        @Override public void enqueue(GameEvent event) { lastEvent = event; }
    }
}