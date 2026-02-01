package cz.matysekxx.aftermathserver.core.world;

import lombok.Getter;


/// Defines the properties of a map tile.
@Getter
public enum TileType {
    VOID(false, false, null),
    WALL(false, false, null),
    FLOOR(true, false, null),
    METRO_TRACK(true, false, null),

    DOOR(true, true, "OPEN"),

    BED(false, true, "REST"),
    CONTAINER(false, true, "LOOT"),
    READABLE(false, true, "READ"),

    UNKNOWN(true, false, null);

    private final boolean walkable;
    private final boolean interactable;
    private final String defaultAction;

    TileType(boolean walkable, boolean interactable, String defaultAction) {
        this.walkable = walkable;
        this.interactable = interactable;
        this.defaultAction = defaultAction;
    }

}
