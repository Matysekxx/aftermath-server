package cz.matysekxx.aftermathserver.core.world;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CollisionIntegrationTest {

    @Autowired
    private WorldManager worldManager;

    @Test
    void testVeleslavinMapCollisions() {
        String mapId = "nadrazi-veleslavin";

        GameMapData map = worldManager.getMap(mapId);
        assertNotNull(map, "Mapa 'nadrazi_veleslavin' se nenačetla!");

        int layer = 1;

        boolean isSpaceWalkable = worldManager.isWalkable(mapId, layer, 0, 0);
        assertTrue(isSpaceWalkable, "Souřadnice [0,0] (mezera) by měla být průchozí, ale server říká NE.");

        boolean isWallWalkable = worldManager.isWalkable(mapId, layer, 40, 0);
        assertFalse(isWallWalkable, "Souřadnice [40,0] ('│') by měla být ZEĎ, ale server říká, že je průchozí!");

        boolean isSpawnWalkable = worldManager.isWalkable(mapId, layer, 42, 2);
        assertTrue(isSpawnWalkable, "Spawn point [42,2] musí být průchozí!");
    }
}