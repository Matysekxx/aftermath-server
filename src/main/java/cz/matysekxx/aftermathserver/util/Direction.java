package cz.matysekxx.aftermathserver.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.ThreadLocalRandom;

/// Represents cardinal directions for movement in the game world.
///
/// Each direction provides the delta change for X and Y coordinates.
@Getter
@AllArgsConstructor
public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    /// The change in X coordinate.
    private final int dx;
    /// The change in Y coordinate.
    private final int dy;

    public static Direction getRandomDirection() {
        return values()[ThreadLocalRandom.current().nextInt(values().length)];
    }
}
