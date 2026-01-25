package cz.matysekxx.aftermathserver.util;

/// Represents a 2D coordinate point.
///
/// Used for grid positions, directions, and distance calculations on a single layer.
///
/// @param x The horizontal coordinate.
/// @param y The vertical coordinate.
public record Vector2(int x, int y) {

    /// Creates a new Vector2 instance.
    ///
    /// @param x The horizontal coordinate.
    /// @param y The vertical coordinate.
    /// @return A new Vector2.
    public static Vector2 of(int x, int y) {
        return new Vector2(x, y);
    }
}