package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private WorldManager worldManager;

    @InjectMocks private StatsService statsService;

    @Test
    void testApplyStats_SafeZoneRegeneration() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        Player player = new Player("p1", "User", new Vector3(0,0,0), config, "letnany", "CLASS");
        player.setMaxHp(100);
        player.setHp(90);
        player.setRads(10);

        GameMapData map = new GameMapData();
        map.setType(MapType.SAFE_ZONE);

        when(worldManager.getMap("letnany")).thenReturn(map);

        boolean changed = statsService.applyStats(player);

        assertTrue(changed);
        assertEquals(91, player.getHp());
    }

    @Test
    void testApplyStats_HazardZoneRadiation() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        Player player = new Player("p1", "User", new Vector3(0,0,0), config, "prosek", "CLASS");
        player.setMaxHp(100);
        player.setHp(100);
        player.setRads(0);
        player.setRadsLimit(50);

        GameMapData map = new GameMapData();
        map.setType(MapType.HAZARD_ZONE);
        map.setDifficulty(6);
        when(worldManager.getMap("prosek")).thenReturn(map);

        statsService.applyStats(player);

        assertEquals(2, player.getRads());
        assertEquals(100, player.getHp());
    }

    @Test
    void testApplyStats_RadiationDamage() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        Player player = new Player("p1", "User", new Vector3(0,0,0), config, "prosek", "CLASS");
        player.setRads(100);
        player.setRadsLimit(50);
        player.setHp(100);

        GameMapData map = new GameMapData();
        map.setType(MapType.HAZARD_ZONE);
        map.setDifficulty(10);

        when(worldManager.getMap("prosek")).thenReturn(map);

        statsService.applyStats(player);

        assertEquals(90, player.getHp());
    }
}