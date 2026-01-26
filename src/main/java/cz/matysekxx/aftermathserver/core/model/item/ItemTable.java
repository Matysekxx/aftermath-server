package cz.matysekxx.aftermathserver.core.model.item;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/// Configuration class holding definitions for all items in the game.
///
/// Loaded from application properties. Acts as a database of item templates.
@Data
@Configuration
@ConfigurationProperties(prefix = "game.items")
public class ItemTable {
    private List<ItemTemplate> resourceItems;
    private List<ItemTemplate> weaponItems;
    private List<ItemTemplate> consumableItems;
    private Map<String, ItemTemplate> itemsById = new HashMap<>();

    /// Initializes the lookup map after properties are set.
    @PostConstruct
    public void init() {
        final Collection<ItemTemplate> allItems = new ArrayList<>();
        if (resourceItems != null) allItems.addAll(resourceItems);
        if (weaponItems != null) allItems.addAll(weaponItems);
        if (consumableItems != null) allItems.addAll(consumableItems);
        itemsById = allItems.stream().collect(Collectors.toMap(ItemTemplate::getId, item -> item));
    }

    /// Retrieves an item template by its ID.
    public ItemTemplate getItemTemplate(String id) {
        return itemsById.get(id);
    }

    public List<ItemTemplate> getDefinitions() {
        return new ArrayList<>(itemsById.values());
    }
}
