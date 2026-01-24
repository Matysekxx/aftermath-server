package cz.matysekxx.aftermathserver.core.model;

import org.springframework.stereotype.Service;

/// Factory for creating item instances.
///
/// Uses `ItemTable` to look up templates and creates new instances with specific quantities.
@Service
public class ItemFactory {
    private final ItemTable itemTable;

    public ItemFactory(ItemTable itemTable) {
        this.itemTable = itemTable;
    }

    /// Creates a new item instance.
    ///
    /// @param id       The item ID.
    /// @param quantity The quantity.
    /// @return A new Item object.
    public Item createItem(String id, int quantity) {
        final Item template = itemTable.getItemTemplate(id);
        if (template == null) {
            throw new IllegalArgumentException("Item template not found: " + id);
        }
        return template.cloneWithQuantity(quantity);
    }
}
