package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.world.Environment;
import cz.matysekxx.aftermathserver.core.world.ParsedMapLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MapLoadPayload {
    private String mapId;
    private String mapName;
    private List<String> levels;
    private Environment environment;
    private List<ParsedMapLayer> parsedLayers;
}