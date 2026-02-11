package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a valid spawn location.
 *
 * @author Matysekxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpawnPointInfo {
    /** The ID of the map. */
    private String mapId;
    /** The display name of the map. */
    private String mapName;
}