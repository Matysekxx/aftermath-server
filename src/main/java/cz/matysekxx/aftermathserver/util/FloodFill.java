package cz.matysekxx.aftermathserver.util;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.TileType;
import cz.matysekxx.aftermathserver.core.world.parser.ParsedMapLayer;
import cz.matysekxx.aftermathserver.core.world.triggers.TeleportTrigger;

import java.util.*;

/**
 * Utility class for calculating reachable areas on the map.
 * <p>
 * Uses the BFS (Breadth-First Search) algorithm to identify all tiles
 * that a player can reach from the starting points (spawns). This is crucial
 * for server-side validation of player movement and for AI pathfinding logic.
 *
 * @author Matysekxx
 */
public final class FloodFill {

    /**
     * Calculates reachable tiles for a given map.
     * <p>
     * The algorithm starts at all defined spawn points of the map and spreads in all
     * directions. It considers terrain walkability and follows teleportation triggers (stairs, links).
     *
     * @param map The map data to be analyzed.
     * @return A list of unique coordinates that are reachable.
     */
    public static List<Vector3> floodFill(GameMapData map) {
        if (map == null) return List.of();

        final Set<Vector3> visited = new HashSet<>();
        final Queue<Vector3> queue = new LinkedList<>();
        for (Vector3 spawn : map.getSpawns().values()) {
            if (isWalkable(map, spawn) && visited.add(spawn)) {
                queue.add(spawn);
            }
        }

        while (!queue.isEmpty()) {
            final Vector3 current = queue.poll();
            if (!isWalkable(map, current)) continue;
            for (Vector3 neighbor : getNeighbors(current)) {
                if (isWalkable(map, neighbor) && visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }

            map.getDynamicTrigger(current.x(), current.y(), current.z())
                    .ifPresent(trigger -> {
                        if (trigger instanceof TeleportTrigger tp) {
                            final Vector3 dest = new Vector3(tp.getTargetX(), tp.getTargetY(), tp.getTargetLayer());
                            if (isWalkable(map, dest) && visited.add(dest)) queue.add(dest);
                        }
                    });
            map.getMaybeTileTrigger(String.valueOf(map.getLayer(current.z()).getSymbolAt(current.x(), current.y())))
                    .ifPresent(trigger -> {
                        if (trigger instanceof TeleportTrigger tp) {
                            final Vector3 dest = new Vector3(tp.getTargetX(), tp.getTargetY(), tp.getTargetLayer());
                            if (isWalkable(map, dest) && visited.add(dest)) queue.add(dest);
                        }
                    });
        }
        return List.copyOf(visited);
    }

    private static List<Vector3> getNeighborsWithPrecision(Vector3 center, GameMapData mapData) {
        return getNeighbors(center).stream().filter(pos -> isWalkable(mapData, pos)).toList();
    }

    /**
     * Gets the four direct neighbors (up, down, left, right) of a given coordinate.
     *
     * @param c The central coordinate.
     * @return A list of neighboring coordinates.
     */
    private static List<Vector3> getNeighbors(Vector3 c) {
        return List.of(
                Vector3.of(c.x(), c.y() - 1, c.z()),
                Vector3.of(c.x(), c.y() + 1, c.z()),
                Vector3.of(c.x() - 1, c.y(), c.z()),
                Vector3.of(c.x() + 1, c.y(), c.z())
        );
    }

    /**
     * Checks if a tile at a given coordinate is walkable.
     *
     * @param map The map data.
     * @param c   The coordinate to check.
     * @return {@code true} if the tile is within bounds and walkable, {@code false} otherwise.
     */
    private static boolean isWalkable(GameMapData map, Vector3 c) {
        final ParsedMapLayer mapLayer = map.getLayer(c.z());
        if (mapLayer == null) return false;
        if (c.y() >= mapLayer.getHeight() || c.y() < 0 || c.x() >= mapLayer.getWidth() || c.x() < 0) return false;
        final TileType tileType = map.getLayer(c.z()).getTileAt(c.x(), c.y());
        if (tileType == TileType.WALL || tileType == TileType.VOID) return false;
        return tileType.isWalkable();
    }
}
