package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.world.Environment;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.ParsedMapLayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/// DTO for sending complete map data to the client.
@Getter
@AllArgsConstructor
public class MapLoadPayload {
    /// The unique identifier of the map.
    private String mapId;
    /// The display name of the map.
    private String mapName;
    /// List of file paths for map layers (layout files).
    private Map<Integer, String> levels;
    /// Environmental settings (radiation, darkness).
    private Environment environment;
    /// The parsed 2D character arrays representing the map layers.
    private Map<Integer, ParsedMapLayer> parsedLayers;

    /// Creates a MapLoadPayload from GameMapData.
    public static MapLoadPayload of(GameMapData gameMapData) {
        return new MapLoadPayload(
                gameMapData.getId(),
                gameMapData.getName(),
                gameMapData.getLayout(),
                gameMapData.getEnvironment(),
                gameMapData.getParsedLayers()
        );
    }
}