package cz.matysekxx.aftermathserver.core.model.item;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration class holding definitions for all items in the game.
 * <p>
 * Loaded from application properties. Acts as a database of item templates.
 *
 * @author Matysekxx
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "game.items")
public class ItemTable {
    /**
     * List of resource item templates.
     */
    private List<ItemTemplate> resourceItems;
    /**
     * List of weapon item templates.
     */
    private List<ItemTemplate> weaponItems;
    /**
     * List of consumable item templates.
     */
    private List<ItemTemplate> consumableItems;
    /**
     * Lookup map for item templates by their ID.
     */
    private Map<String, ItemTemplate> itemsById = new HashMap<>();

    /**
     * Initializes the lookup map after properties are set.
     */
    @PostConstruct
    public void init() {
        final Collection<ItemTemplate> allItems = new ArrayList<>();
        if (resourceItems != null) allItems.addAll(resourceItems);
        if (weaponItems != null) allItems.addAll(weaponItems);
        if (consumableItems != null) allItems.addAll(consumableItems);
        itemsById = allItems.stream().collect(Collectors.toMap(ItemTemplate::getId, item -> item));
    }

    /**
     * Retrieves an item template by its ID.
     *
     * @param id The item ID.
     * @return The item template, or null if not found.
     */
    public ItemTemplate getItemTemplate(String id) {
        return itemsById.get(id);
    }

    /**
     * Returns all loaded item templates.
     *
     * @return A list of all item templates.
     */
    public List<ItemTemplate> getDefinitions() {
        return new ArrayList<>(itemsById.values());
    }
}
