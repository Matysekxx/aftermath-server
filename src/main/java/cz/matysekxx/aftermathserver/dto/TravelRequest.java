package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for metro travel requests from the client.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class TravelRequest {
    /** The ID of the destination map (station). */
    private String mapId;
    /** The ID of the metro line being used. */
    private String lineId;
}
