package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;

/// Abstract base class for handling tile triggers.
///
/// Implementations define specific behavior when a player steps on a special tile.
public abstract sealed class TriggerHandler permits ConditionalTeleportHandler, DamageHandler, HealHandler, MetroEntryHandler, TeleportHandler {
    /// Processes the trigger effect on the player.
    ///
    /// @param player The player triggering the tile.
    /// @param tileTrigger The trigger data associated with the tile.
    /// @return true if the trigger was handled successfully, false otherwise.
    public abstract boolean handle(Player player, TileTrigger tileTrigger);
}
