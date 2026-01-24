package cz.matysekxx.aftermathserver.core.world;

import cz.matysekxx.aftermathserver.core.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/// Represents an interactive object on the map.
@Data
@NoArgsConstructor
public class MapObject {
    private String id;
    private String type;
    private int x, y;
    private String action;
    private String description;
    private Collection<Item> items = new ArrayList<>();
}
