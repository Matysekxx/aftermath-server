package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.ConditionalTeleportTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import org.springframework.stereotype.Component;

@Component("CONDITIONAL_TELEPORT")
public non-sealed class ConditionalTeleportHandler extends TriggerHandler {
    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        if (tileTrigger instanceof ConditionalTeleportTrigger conditionalTeleportTrigger) {
            if (conditionalTeleportTrigger.getPredicate() == null ||
                    conditionalTeleportTrigger.getPredicate().test(player)) {
                player.setLayerIndex(conditionalTeleportTrigger.getTargetLayer());
                player.setX(conditionalTeleportTrigger.getTargetX());
                player.setY(conditionalTeleportTrigger.getTargetY());
                return true;
            }
        }
        return false;
    }
}
