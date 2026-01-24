package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Handles looting interactions.
///
/// Transfers items from a container object to the player's inventory.
@Component("LOOT")
public class LootLogic implements InteractionLogic {
    /// Executes the loot interaction.
    ///
    /// Checks if container is empty, moves items to player inventory if space allows,
    /// and updates the container description.
    @Override
    public synchronized Collection<GameEvent> interact(MapObject target, Player player) {
        if (target.getItems().isEmpty()) {
            return List.of(GameEvent.create(EventType.SEND_MESSAGE, target.getDescription() + " - It is empty", player.getId(), null, false));
        }

        final StringBuilder message = new StringBuilder(target.getDescription() + "\nYou found:");
        final Collection<Item> itemsToRemove = new ArrayList<>();

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

        final Collection<GameEvent> events = new ArrayList<>();
        events.add(GameEvent.create(EventType.SEND_INVENTORY, player, player.getId(), player.getMapId(), false));
        events.add(GameEvent.create(EventType.SEND_MESSAGE, message.toString(), player.getId(), null, false));

        return events;
    }
}