package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StatsResponse {
    private int hp;
    private int maxHp;
    private int rads;

    public static StatsResponse of(Player player) {
        return new StatsResponse(player.getHp(), player.getMaxHp(), player.getRads());
    }
}