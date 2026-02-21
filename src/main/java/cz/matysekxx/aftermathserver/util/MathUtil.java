package cz.matysekxx.aftermathserver.util;

/**
 * Utility class for mathematical calculations related to the game world.
 * Provides methods for distance calculations between coordinates.
 *
 * @author Matysekxx
 */
public final class MathUtil {
    private MathUtil() {
    }

    /**
     * Calculates the Manhattan distance (|dx| + |dy|).
     * Ideal for grid-based movement where diagonals are not allowed.
     *
     * @param a Start point.
     * @param b End point.
     * @return Manhattan distance.
     */
    public static int getManhattanDistance(Vector2 a, Vector2 b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /**
     * Calculates Euclidean distance.
     * Ideal for checking radius (e.g. interaction range, explosion range).
     *
     * @param a Start point.
     * @param b End point.
     * @return Euclidean distance.
     */
    public static double getDistance(Vector2 a, Vector2 b) {
        return Math.sqrt(Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2));
    }

    /**
     * Calculates the Chebyshev distance (max(|dx|, |dy|)).
     * Useful for grid-based logic where diagonals count as 1 step.
     *
     * @param a Start point.
     * @param b End point.
     * @return Chebyshev distance.
     */
    public static int getChebyshevDistance(Vector2 a, Vector2 b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }

    public static int getChebyshevDistance(Spatial s1, Spatial s2) {
        return getChebyshevDistance(
                new Vector2(s1.getX(), s1.getY()),
                new Vector2(s2.getX(), s2.getY())
        );
    }
}
