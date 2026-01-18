package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;

public interface TriggerHandler {
    boolean handle(Player player, TileTrigger tileTrigger);
}
