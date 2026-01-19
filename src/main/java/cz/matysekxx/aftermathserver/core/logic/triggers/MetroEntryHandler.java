package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import org.springframework.stereotype.Component;

@Component("METRO_ENTRY")
public non-sealed class MetroEntryHandler extends TriggerHandler {
    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        player.setState(Player.State.TRAVELLING);
        player.setHp(player.getMaxHp());
        return true;
    }
}
