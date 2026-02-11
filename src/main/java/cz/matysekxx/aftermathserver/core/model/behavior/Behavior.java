package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

import java.util.Collection;

/**
 * Interface for NPC AI behavior strategies.
 *
 * @author Matysekxx
 */
public interface Behavior {
    /**
     * Updates the NPC state based on the current environment and nearby players.
     *
     * @param npc     The NPC instance to update.
     * @param map     The map data where the NPC is located.
     * @param players Collection of players currently on the same map.
     */
    void update(Npc npc, GameMapData map, Collection<Player> players);
}