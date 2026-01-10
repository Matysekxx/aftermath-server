package cz.matysekxx.aftermathserver.core.model;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Configuration
@ConfigurationProperties(prefix = "game.items")
public class ItemTable {
    private List<Item> resourceItems;
    private List<Item> weaponItems;
    private List<Item> consumableItems;
    private Map<String, Item> itemsById = new HashMap<>();

    @PostConstruct
    public void init() {
        final List<Item> allItems = new ArrayList<>();
        allItems.addAll(resourceItems);
        allItems.addAll(weaponItems);
        allItems.addAll(consumableItems);
        itemsById = allItems.stream().collect(Collectors.toMap(Item::getId, item -> item));
    }

    public Item getItemTemplate(String id) {
        return itemsById.get(id);
    }
}
