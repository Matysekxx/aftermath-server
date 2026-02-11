package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.config.PlayerClassConfig;
import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.dto.MoveRequest;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.Vector2;
import cz.matysekxx.aftermathserver.util.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock private WorldManager worldManager;
    @Mock private GameEventQueue gameEventQueue;

    @InjectMocks private MovementService movementService;

    @Test
    void testMovementProcess_Obstacle() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        Player player = new Player("session-1", "User", new Vector3(5,5,0), config, "letnany", "SOLDIER");

        MoveRequest request = new MoveRequest("RIGHT");
        when(worldManager.isWalkable("letnany", 0, 6, 5)).thenReturn(false);

        movementService.movementProcess(player, request);

        assertEquals(5, player.getX());
        verify(gameEventQueue).enqueue(argThat(event -> event.type().name().equals("SEND_ERROR")));
    }

    @Test
    void testMovementProcess_ValidMove() {
        PlayerClassConfig config = new PlayerClassConfig();
        config.setMaxHp(100);
        config.setInventoryCapacity(10);
        config.setMaxWeight(50.0);
        Player player = new Player("session-1", "User", new Vector3(5,5,0), config, "map1", "SOLDIER");

        GameMapData mapData = mock(GameMapData.class);
        ParsedMapLayer layer = mock(ParsedMapLayer.class);

        MoveRequest request = new MoveRequest("UP");

        when(worldManager.isWalkable("map1", 0, 5, 4)).thenReturn(true);
        when(worldManager.getMap("map1")).thenReturn(mapData);
        when(mapData.getLayer(0)).thenReturn(layer);
        when(layer.getSymbolAt(any(Vector2.class))).thenReturn('.');
        when(mapData.getDynamicTrigger(5, 4, 0)).thenReturn(Optional.empty());
        when(mapData.getMaybeTileTrigger(".")).thenReturn(Optional.empty());

        when(layer.getWidth()).thenReturn(100);
        when(layer.getHeight()).thenReturn(100);

        movementService.movementProcess(player, request);

        assertEquals(5, player.getX());
        assertEquals(4, player.getY());
        verify(gameEventQueue, atLeastOnce()).enqueue(any());
    }
}