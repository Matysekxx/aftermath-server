package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.GameEvent;

import java.util.Collection;

/// Interface for defining interaction behavior with map objects.
public interface InteractionLogic {
    /// Performs the interaction logic.
    ///
    /// @param target The map object being interacted with.
    /// @param player The player performing the interaction.
    /// @return A list of GameEvents resulting from the interaction.
    Collection<GameEvent> interact(MapObject target, Player player);
}
