package cz.matysekxx.aftermathserver.util;

/**
 * Interface for objects that have a position in the 3D game world.
 * Used for spatial indexing and distance calculations.
 *
 * @author Matysekxx
 */
public interface Spatial {
    int getX();

    int getY();

    int getLayerIndex();
}