package cz.matysekxx.aftermathserver.core.logic.interactions.object;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Handles looting interactions.
///
/// Transfers items from a container object to the player's inventory.
@Component("LOOT")
public class LootLogicObject implements ObjectInteractionLogic {
    private final WorldManager worldManager;

    public LootLogicObject(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /// Executes the loot interaction.
    ///
    /// Checks if container is empty, moves items to player inventory if space allows,
    /// and updates the container description.
    @Override
    public synchronized Collection<GameEvent> interact(MapObject target, Player player) {
        if (target.getItems().isEmpty()) {
            return List.of(GameEventFactory.sendMessageEvent(target.getDescription() + " - It is empty", player.getId()));
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

        final Collection<GameEvent> events = new ArrayList<>();
        events.add(GameEventFactory.sendInventoryEvent(player));
        events.add(GameEventFactory.sendMessageEvent(message.toString(), player.getId()));

        if (target.getItems().isEmpty()) {
            final var maybeMap = worldManager.getMaybeMap(target.getId());
            if (maybeMap.isPresent()) {
                maybeMap.get().getObjects().remove(target);
                events.add(GameEventFactory.broadcastMapObjects(maybeMap.get().getObjects(), player.getMapId()));
            }
        }

        return events;
    }
}