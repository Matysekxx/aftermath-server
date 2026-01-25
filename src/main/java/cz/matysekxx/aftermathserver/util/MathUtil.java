package cz.matysekxx.aftermathserver.util;

public final class MathUtil {
    private MathUtil() {}

    /// Calculates the Manhattan distance (|dx| + |dy|).
    /// Ideal for grid-based movement (no diagonals).
    public static int getManhattanDistance(Vector2 a, Vector2 b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
    }

    /// Calculates Euclidean distance.
    /// Ideal for checking radius (e.g. interaction range, explosion range).
    public static double getDistance(Vector2 a, Vector2 b) {
        return Math.sqrt(Math.pow(a.x() - b.x(), 2) + Math.pow(a.y() - b.y(), 2));
    }

    /// Calculates the Chebyshev distance (max(|dx|, |dy|)).
    /// Useful for grid-based logic where diagonals count as 1 step (King's move).
    public static int getChebyshevDistance(Vector2 a, Vector2 b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.y() - b.y()));
    }
}
