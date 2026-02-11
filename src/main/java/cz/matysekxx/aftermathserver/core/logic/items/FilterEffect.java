package cz.matysekxx.aftermathserver.core.logic.items;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import org.springframework.stereotype.Component;

/**
 * Logic for items that increase the player's radiation limit (Filters).
 *
 * @author Matysekxx
 */
@Component("FILTER")
public class FilterEffect implements ItemEffect {
    @Override
    public boolean apply(Player player, Item item) {
        final int increase = item.getHealAmount() != null ? item.getHealAmount() : 50;
        player.setRadsLimit(player.getRadsLimit() + increase);
        return true;
    }
}