package cz.matysekxx.aftermathserver.util;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.triggers.TeleportTrigger;

import java.util.*;

/// Utility class for calculating reachable areas on the map.
///
/// Uses the BFS (Breadth-First Search) algorithm to identify all tiles
/// that a player can reach from the starting points (spawns).
public final class FloodFill {

    /// Calculates reachable tiles for a given map.
    ///
    /// The algorithm starts at all defined spawn points of the map and spreads in all
    /// directions. It considers terrain walkability and follows teleportation triggers (stairs, links).
    ///
    /// @param map The map data to be analyzed.
    /// @return A list of unique coordinates that are reachable.
    public static List<Coordination> floodFill(GameMapData map) {
        if (map == null) return List.of();

        final Set<Coordination> visited = new HashSet<>();
        final Queue<Coordination> queue = new LinkedList<>();
        for (Coordination spawn : map.getSpawns().values()) {
            if (isWalkable(map, spawn) && visited.add(spawn)) {
                queue.add(spawn);
            }
        }

        while (!queue.isEmpty()) {
            final Coordination current = queue.poll();
            for (Coordination neighbor : getNeighbors(current)) {
                if (isWalkable(map, neighbor) && visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }

            map.getDynamicTrigger(current.x(), current.y(), current.z())
                    .ifPresent(trigger -> {
                        if (trigger instanceof TeleportTrigger tp) {
                            final Coordination dest = new Coordination(tp.getTargetX(), tp.getTargetY(), tp.getTargetLayer());
                            if (isWalkable(map, dest) && visited.add(dest)) queue.add(dest);
                        }
                    });
        }

        return new ArrayList<>(visited);
    }

    private static List<Coordination> getNeighbors(Coordination c) {
        return List.of(
                new Coordination(c.x(), c.y() - 1, c.z()),
                new Coordination(c.x(), c.y() + 1, c.z()),
                new Coordination(c.x() - 1, c.y(), c.z()),
                new Coordination(c.x() + 1, c.y(), c.z())
        );
    }

    private static boolean isWalkable(GameMapData map, Coordination c) {
        if (c.z() < 0 || c.z() >= map.getLayerCount()) return false;
        return map.getLayer(c.z()).getTileAt(c.x(), c.y()).isWalkable();
    }
}
