package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// DTO for sending player statistics to the client.
@Getter
@AllArgsConstructor
public class StatsResponse {
    /// Current health points.
    private int hp;
    /// Maximum health points.
    private int maxHp;
    /// Current radiation level.
    private int rads;

    /// Creates a StatsResponse from a Player entity.
    public static StatsResponse of(Player player) {
        return new StatsResponse(player.getHp(), player.getMaxHp(), player.getRads());
    }
}