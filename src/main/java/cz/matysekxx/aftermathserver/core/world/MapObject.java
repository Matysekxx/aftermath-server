package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.util.Spatial;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/// Represents an interactive object on the map.
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
