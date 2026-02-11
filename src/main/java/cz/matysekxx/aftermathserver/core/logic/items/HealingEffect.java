package cz.matysekxx.aftermathserver.core.logic.items;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import org.springframework.stereotype.Component;

/**
 * Logic for items that restore health (Medkits, Food).
 *
 * @author Matysekxx
 */
@Component("HEAL")
public class HealingEffect implements ItemEffect {
    @Override
    public boolean apply(Player player, Item item) {
        if (player.getHp() >= player.getMaxHp()) return false;
        final int amount = item.getHealAmount() != null ? item.getHealAmount() : 0;
        player.setHp(Math.min(player.getMaxHp(), player.getHp() + amount));
        return true;
    }
}