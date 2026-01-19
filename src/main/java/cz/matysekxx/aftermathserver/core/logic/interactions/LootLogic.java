package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("LOOT")
public class LootLogic implements InteractionLogic {
    @Override
    public synchronized List<GameEvent> interact(MapObject target, Player player) {
        if (target.getItems().isEmpty()) {
            return List.of(GameEvent.create(EventType.SEND_MESSAGE, target.getDescription() + " - It is empty", player.getId(), null, false));
        }

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

        final List<GameEvent> events = new ArrayList<>();
        events.add(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
        events.add(GameEvent.create(EventType.SEND_MESSAGE, message.toString(), player.getId(), null, false));

        return events;
    }
}