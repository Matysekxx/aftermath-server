package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MapObjectFactory {

    private String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public MapObject createPlayerCorpse(Player deadPlayer) {
        final MapObject corpse = new MapObject();
        corpse.setId("corpse_" + deadPlayer.getUsername());
        corpse.setType("CONTAINER");
        corpse.setAction("LOOT");
        corpse.setDescription("Dead body player " + deadPlayer.getUsername());

        corpse.setX(deadPlayer.getX());
        corpse.setY(deadPlayer.getY());

        final List<Item> droppedItems = new ArrayList<>(deadPlayer.getInventory().getSlots().values());
        corpse.setItems(droppedItems);
        return corpse;
    }

}
