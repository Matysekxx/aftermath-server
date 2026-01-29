package cz.matysekxx.aftermathserver.core.logic.items;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import org.springframework.stereotype.Component;

/// Logic for items that reduce radiation levels.
@Component("ANTIRAD")
public class RadiationEffect implements ItemEffect {
    @Override
    public boolean apply(Player player, Item item) {
        if (player.getRads() <= 0) return false;
        final int reduction = item.getHealAmount() != null ? item.getHealAmount() : 20;
        player.setRads(Math.max(0, player.getRads() - reduction));
        return true;
    }
}