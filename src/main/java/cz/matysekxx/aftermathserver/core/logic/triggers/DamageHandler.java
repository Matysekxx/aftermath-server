package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.DamageTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import org.springframework.stereotype.Component;

/// Handles damage triggers.
///
/// Inflicts damage to the player when stepping on a hazard tile (e.g., fire, spikes).
@Component("DAMAGE")
public non-sealed class DamageHandler extends TriggerHandler {
    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        final DamageTrigger damageTrigger = (DamageTrigger) tileTrigger;
        player.setHp(player.getHp() - damageTrigger.getDamage());
        return true;
    }
}