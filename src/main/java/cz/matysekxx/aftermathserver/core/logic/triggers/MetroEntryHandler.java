package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.TileTrigger;

public class MetroEntryHandler implements  TriggerHandler {
    public boolean handle(Player player, TileTrigger tileTrigger) {
        return false;
    }

    @Override
    public String getType() {
        return "";
    }
}
