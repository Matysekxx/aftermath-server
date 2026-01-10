package cz.matysekxx.aftermathserver.core.world;

import lombok.Getter;


@Getter
public enum TileType {
    WALL(false, false, null),
    FLOOR(true, false, null),
    EMPTY(true, false, null),
    VOID(false, false, null),

    DOOR(true, true, "OPEN"),
    BED(false, true, "REST"),
    ELEVATOR(true, true, "TRAVEL"),

    COMPUTER(false, true, "READ"),
    RADIO(false, true, "READ"),
    SERVER(false, true, "READ"),

    ARMORY(false, true, "LOOT"),
    WEAPON(false, true, "LOOT"),
    FOOD(false, true, "LOOT"),
    OVEN(false, true, "LOOT"),

    METRO_TRACK(true, false, null),
    UNKNOWN(false, false, null);

    private final boolean walkable;
    private final boolean interactable;
    private final String defaultAction;

    TileType(boolean walkable, boolean interactable, String defaultAction) {
        this.walkable = walkable;
        this.interactable = interactable;
        this.defaultAction = defaultAction;
    }

}
