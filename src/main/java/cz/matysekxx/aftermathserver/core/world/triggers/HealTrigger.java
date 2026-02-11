package cz.matysekxx.aftermathserver.core.world.triggers;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Trigger definition for healing tiles.
 *
 * @author Matysekxx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HealTrigger extends TileTrigger {
    /** The amount of health to restore. */
    private int healAmount;

    @Override
    public void onEnter(Player player, TriggerContext context) {
        player.setHp(Math.min(player.getMaxHp(), player.getHp() + healAmount));
    }
}