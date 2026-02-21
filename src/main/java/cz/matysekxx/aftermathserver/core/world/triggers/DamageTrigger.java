package cz.matysekxx.aftermathserver.core.world.triggers;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Trigger definition for damaging tiles.
 *
 * @author Matysekxx
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DamageTrigger extends TileTrigger {
    /**
     * The amount of damage to deal.
     */
    private int damage;

    @Override
    public void onEnter(Player player, TriggerContext context) {
        player.setHp(player.getHp() - damage);
    }
}