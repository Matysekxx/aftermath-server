package cz.matysekxx.aftermathserver.core.logic.triggers;

import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.triggers.TileTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("METRO_ENTRY")
public non-sealed class MetroEntryHandler extends TriggerHandler {
    private final MetroService metroService;

    public MetroEntryHandler(MetroService metroService) {
        this.metroService = metroService;
    }

    @Override
    public boolean handle(Player player, TileTrigger tileTrigger) {
        player.setState(Player.State.TRAVELLING);
        player.setHp(player.getMaxHp());
        if (metroService != null) log.info("success");
        return true;
    }
}
