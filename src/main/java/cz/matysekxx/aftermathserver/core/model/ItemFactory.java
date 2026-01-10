package cz.matysekxx.aftermathserver.core.model;

import org.springframework.stereotype.Service;

@Service
public class ItemFactory {
    private final ItemTable itemTable;

    public ItemFactory(ItemTable itemTable) {
        this.itemTable = itemTable;
    }

    public Item createItem(String id, int quantity) {
        final Item template = itemTable.getItemTemplate(id);
        if (template == null) {
            throw new IllegalArgumentException("Item template not found: " + id);
        }
        return template.cloneWithQuantity(quantity);
    }
}
