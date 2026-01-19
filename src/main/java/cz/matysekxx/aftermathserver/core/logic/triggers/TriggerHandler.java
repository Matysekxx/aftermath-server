package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;

public abstract sealed class TriggerHandler permits DamageHandler, HealHandler, MetroEntryHandler, TeleportHandler {
    public abstract boolean handle(Player player, TileTrigger tileTrigger);
}
