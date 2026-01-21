package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.model.metro.MetroStation;
import cz.matysekxx.aftermathserver.core.world.triggers.MetroTrigger;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("METRO_ENTRY")
public non-sealed class MetroEntryHandler extends TriggerHandler {
    private final MetroService metroService;

    public MetroEntryHandler(MetroService metroService) {
        this.metroService = metroService;
    }

    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        if (tileTrigger instanceof MetroTrigger metroTrigger) {
            metroService.handleStationTrigger(player, metroTrigger.getLineId());
            return true;
        }
        return false;
    }
}
