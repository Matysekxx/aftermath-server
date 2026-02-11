package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import lombok.Data;

import java.util.List;

/**
 * DTO for sending metro UI data (list of stations) to the client.
 *
 * @author Matysekxx
 */
@Data
public class UILoadResponse {
    /** List of available metro stations on the current line. */
    private List<MetroStation> stations;
    /** The ID of the current metro line. */
    private String lineId;
}
