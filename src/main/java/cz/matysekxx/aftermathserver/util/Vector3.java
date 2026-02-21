package cz.matysekxx.aftermathserver.util;

/**
 * Represents a 3D coordinate point in the game world.
 * <p>
 * Includes the layer index (z) to identify the specific map depth or floor level.
 *
 * @param x The horizontal position.
 * @param y The vertical position.
 * @param z The layer index (Z-coordinate).
 * @author Matysekxx
 */
public record Vector3(int x, int y, int z) {

    /**
     * Creates a new Vector3 instance.
     *
     * @param x The horizontal position.
     * @param y The vertical position.
     * @param z The layer index.
     * @return A new Vector3.
     */
    public static Vector3 of(int x, int y, int z) {
        return new Vector3(x, y, z);
    }
}
