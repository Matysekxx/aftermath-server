package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/// DTO representing a valid spawn location.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpawnPointInfo {
    private String mapId;
    private String mapName;
}