package cz.matysekxx.aftermathserver.core.logic.items;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;

/**
 * Interface for defining the behavior of usable items.
 *
 * @author Matysekxx
 */
public interface ItemEffect {
    /**
     * Applies the effect of the item to the player.
     *
     * @param player The player using the item.
     * @param item   The item instance being consumed.
     * @return true if the effect was applied successfully.
     */
    boolean apply(Player player, Item item);
}