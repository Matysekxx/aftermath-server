package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.factory.MapObjectFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemType;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CombatServiceTest {

    @Mock private WorldManager worldManager;
    @Mock private GameEventQueue gameEventQueue;

    @InjectMocks private CombatService combatService;

    private Player player;
    private Npc npc;
    private GameMapData map;

    @BeforeEach
    void setUp() {
        player = mock(Player.class);
        npc = mock(Npc.class);
        map = mock(GameMapData.class);

        lenient().when(player.getMapId()).thenReturn("letnany");
        lenient().when(player.getId()).thenReturn("player-1");
        lenient().when(worldManager.getMap("letnany")).thenReturn(map);
    }

    @Test
    void testHandleAttack_NoWeaponEquipped() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        player = new Player("player-1", "Hero", new Vector3(0,0,0), config, "letnany", "SOLDIER");
        player.setEquippedWeaponSlot(null);

        combatService.handleAttack(player);

        verify(gameEventQueue).enqueue(argThat(event -> event.type().name().equals("SEND_ERROR")));
    }

    @Test
    void testHandleAttack_SuccessfulHit() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        player = new Player("player-1", "Hero", new Vector3(0,0,0), config, "letnany", "SOLDIER");

        Item weapon = Item.builder()
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

        when(worldManager.getMap("letnany")).thenReturn(map);

        combatService.handleAttack(player);

        assertEquals(90, npc.getHp());
        assertTrue(player.getLastAttackTime() > 0);
    }
}