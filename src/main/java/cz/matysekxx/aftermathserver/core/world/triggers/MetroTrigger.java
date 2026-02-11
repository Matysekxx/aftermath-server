package cz.matysekxx.aftermathserver.core.world.triggers;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Trigger definition for metro station entry.
 *
 * @author Matysekxx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetroTrigger extends TileTrigger {
    /** The ID of the metro line associated with this trigger. */
    private String lineId;

    @Override
    public void onEnter(Player player, TriggerContext context) {
        context.metroService().handleStationTrigger(player, lineId);
    }
}