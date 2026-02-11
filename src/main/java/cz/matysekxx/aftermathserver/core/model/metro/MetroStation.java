package cz.matysekxx.aftermathserver.core.model.metro;

import lombok.Data;

/**
 * Represents a single station in the metro network.
 *
 * @author Matysekxx
 */
@Data
public class MetroStation {
    /** Unique identifier for the station (usually map ID). */
    private String id;
    /** Display name of the station. */
    private String name;
}
