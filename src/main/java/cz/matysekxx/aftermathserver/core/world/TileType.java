package cz.matysekxx.aftermathserver.core.world;

import lombok.Getter;


/// Defines the properties of a map tile.
@Getter
public enum TileType {
    VOID(false, null),
    WALL(false, null),
    FLOOR(true, null),
    METRO_TRACK(true, null),

    DOOR(true, "OPEN"),

    BED(false, "REST"),
    CONTAINER(false, "LOOT"),
    READABLE(false, "READ"),

    UNKNOWN(false, null);

    private final boolean walkable;
    private final String defaultAction;

    TileType(boolean walkable, String defaultAction) {
        this.walkable = walkable;
        this.defaultAction = defaultAction;
    }

}
