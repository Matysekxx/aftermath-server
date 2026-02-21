package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for sending player position updates.
 *
 * @author Matysekxx
 */
@Getter
@AllArgsConstructor
public class PlayerUpdatePayload {
    /**
     * The ID of the player.
     */
    private String playerId;
    /**
     * The X coordinate.
     */
    private int x;
    /**
     * The Y coordinate.
     */
    private int y;
    /**
     * The Z coordinate (layer index).
     */
    private int z;

    /**
     * Creates a PlayerUpdatePayload from a Player entity.
     *
     * @param player The player entity.
     * @return The payload.
     */
    public static PlayerUpdatePayload of(Player player) {
        return new PlayerUpdatePayload(player.getId(), player.getX(), player.getY(), player.getLayerIndex());
    }
}