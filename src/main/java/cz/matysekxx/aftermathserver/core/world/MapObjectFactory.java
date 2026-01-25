package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemFactory;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Factory for creating dynamic map objects.
@Service
public class MapObjectFactory {

    private final ItemFactory itemFactory;

    public MapObjectFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /// Creates a corpse container from a dead player.
    public MapObject createPlayerCorpse(Player deadPlayer) {
        final MapObject corpse = new MapObject();
        corpse.setId("corpse_" + deadPlayer.getName());
        corpse.setType("CONTAINER");
        corpse.setAction("LOOT");
        corpse.setDescription("Dead body player " + deadPlayer.getName());

        corpse.setX(deadPlayer.getX());
        corpse.setY(deadPlayer.getY());

        final List<Item> droppedItems = new ArrayList<>(deadPlayer.getInventory().getSlots().values());
        corpse.setItems(droppedItems);
        return corpse;
    }

    /// Creates a loot bag containing a specific item.
    public MapObject createLootBag(String itemId, int quantity, int x, int y) {
        final MapObject bag = new MapObject();
        bag.setId(generateId("loot"));
        bag.setType("CONTAINER");
        bag.setAction("LOOT");
        bag.setDescription("Dropped items");
        bag.setX(x);
        bag.setY(y);
        bag.getItems().add(itemFactory.createItem(itemId, quantity));
        return bag;
    }

}
