package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

import java.util.Collection;

/**
 * Behavior for NPCs that stay in one place (e.g., guards, shopkeepers).
 *
 * @author Matysekxx
 */
public class StationaryBehavior implements Behavior {
    /**
     * Updates the NPC state. Stationary NPCs do not move or act on their own.
     *
     * @param npc     The NPC instance to update.
     * @param map     The map data where the NPC is located.
     * @param players Collection of players currently on the same map.
     */
    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
    }
}
