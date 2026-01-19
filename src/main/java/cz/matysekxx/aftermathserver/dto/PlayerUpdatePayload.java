package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerUpdatePayload {
    private String playerId;
    private int x;
    private int y;
    private int z;

    public static PlayerUpdatePayload of(Player player) {
        return new PlayerUpdatePayload(player.getId(), player.getX(), player.getY(), player.getLayerIndex());
    }
}