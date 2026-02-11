package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.util.Spatial;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an interactive object placed on the game map.
 * <p>
 * Map objects can be containers (loot), readable signs, or furniture.
 * They implement {@link Spatial} for positioning and indexing.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class MapObject implements Spatial {
    private String id;
    private String type;
    private int x, y;
    private int z;
    private String action;
    private String description;
    private Collection<Item> items = new ArrayList<>();

    public int getLayerIndex() {
        return z;
    }
}
