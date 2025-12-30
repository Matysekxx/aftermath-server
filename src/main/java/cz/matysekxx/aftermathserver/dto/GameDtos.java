package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.Environment;
import cz.matysekxx.aftermathserver.core.world.ParsedMapLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class GameDtos {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MoveReq {
        private String direction;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ChatReq {
        private String message;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class InteractReq {
        private String type;
        private String target;
    }


    @Getter @AllArgsConstructor
    public static class PlayerUpdatePayload {
        private String playerId;
        private int x;
        private int y;
    }

    @Getter
    @AllArgsConstructor
    public static class MapLoadPayload {
        private String mapId;
        private String mapName;
        private List<String> levels;
        private Environment environment;
        private List<ParsedMapLayer> parsedLayers;
    }
    @Getter @AllArgsConstructor
    public static class StatsResponse {
        private int hp;
        private int maxHp;
        private int rads;

        public static StatsResponse of(Player player) { return new StatsResponse(player.getHp(), player.getMaxHp(), player.getRads()); }
    }
}