package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.HealTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import org.springframework.stereotype.Component;

/// Handles healing triggers.
///
/// Restores player health when stepping on a healing tile.
@Component("HEAL")
public non-sealed class HealHandler extends TriggerHandler {
    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        if (tileTrigger instanceof HealTrigger healTrigger) {
            player.setHp(Math.min(player.getHp() + healTrigger.getHealAmount(), player.getMaxHp()));
            return true;
        }
        return false;
    }
}
