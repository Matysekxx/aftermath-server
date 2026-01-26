package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

import java.util.Collection;

/// Behavior for NPCs that patrol between points.
public class PatrolBehavior implements Behavior {
    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
        // TODO: Implement patrol logic (moving between predefined waypoints)
    }
}
