package cz.matysekxx.aftermathserver.core.logic;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("LOOT")
public class LootLogic implements InteractionLogic {
    @Override
    public synchronized WebSocketResponse interact(MapObject target, Player player) {
        if (target.getItems().isEmpty())
            return WebSocketResponse.of("NOTIFICATION", target.getDescription() + "It is empty");

        final StringBuilder message = new StringBuilder(target.getDescription() + "\nYou found:");
        final List<Item> itemsToRemove = new ArrayList<>();

        for (Item item : target.getItems()) {
            if (player.getInventory().addItem(item)) {
                message.append("\n + ").append(item.getQuantity()).append("x ").append(item.getName());
                itemsToRemove.add(item);
            } else {
                message.append("\n ! ").append(item.getName()).append("It is too heavy for you");
            }
        }

        target.getItems().removeAll(itemsToRemove);

        if (target.getItems().isEmpty()) {
            target.setDescription("Empty");
        }
        return WebSocketResponse.of("LOOT_SUCCESS", message.toString());

    }
}