package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TeleportTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import org.springframework.stereotype.Component;

/// Handles teleport triggers.
///
/// Instantly moves the player to a specified location and layer.
@Component("TELEPORT")
public non-sealed class TeleportHandler extends TriggerHandler {
    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        if (tileTrigger instanceof TeleportTrigger teleportTrigger) {
            player.setLayerIndex(teleportTrigger.getTargetLayer());
            player.setX(teleportTrigger.getTargetX());
            player.setY(teleportTrigger.getTargetY());
            return true;
        }
        return false;
    }
}
