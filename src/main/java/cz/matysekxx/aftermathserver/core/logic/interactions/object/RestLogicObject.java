package cz.matysekxx.aftermathserver.core.logic.interactions.object;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/// Handles resting interactions.
///
/// When a player interacts with a "REST" object (like a bed), their health is fully restored,
/// radiation is cleared, and their position is synchronized with the object.
@Component("REST")
public class RestLogicObject implements ObjectInteractionLogic {
    /// Executes the rest interaction.
    ///
    /// Fully heals the player, removes all radiation, and moves the player to the target's coordinates.
    ///
    /// @param target The object being used for rest (e.g., a bed).
    /// @param player The player performing the rest action.
    /// @return A collection containing the stats update event to be sent to the client.
    @Override
    public Collection<GameEvent> interact(MapObject target, Player player) {
        player.setHp(player.getMaxHp());
        player.setRads(0);
        return List.of(GameEventFactory.sendStatsEvent(player));
    }
}
