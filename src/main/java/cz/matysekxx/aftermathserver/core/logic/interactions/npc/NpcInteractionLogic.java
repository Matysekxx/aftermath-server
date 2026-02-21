package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;

import java.util.Collection;

/**
 * Interface for defining interaction logic with NPCs.
 *
 * @author Matysekxx
 */
public interface NpcInteractionLogic {

    /**
     * Executes the interaction between a player and an NPC.
     *
     * @param target The NPC being interacted with.
     * @param player The player initiating the interaction.
     * @return A collection of game events resulting from the interaction.
     */
    Collection<GameEvent> interact(Npc target, Player player);

    /**
     * @return The type of interaction this logic handles.
     */
    InteractionType getType();
}
