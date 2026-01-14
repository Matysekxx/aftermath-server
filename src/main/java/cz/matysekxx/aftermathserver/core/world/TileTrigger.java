package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

@Data
public class TileTrigger {
    private String type;
    private String attribute;

    private String targetMapId;
    private String targetSpawnSymbol;
    private String routeId;
}