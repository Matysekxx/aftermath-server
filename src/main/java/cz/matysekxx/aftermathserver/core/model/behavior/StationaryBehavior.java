package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

import java.util.Collection;

/// Behavior for NPCs that stay in one place (e.g., guards, shopkeepers).
public class StationaryBehavior implements Behavior {
    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
    }
}
