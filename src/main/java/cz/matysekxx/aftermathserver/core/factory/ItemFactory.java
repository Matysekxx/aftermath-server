package cz.matysekxx.aftermathserver.core.factory;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemTable;
import cz.matysekxx.aftermathserver.core.model.item.ItemTemplate;
import org.springframework.stereotype.Service;

/**
 * Factory service for creating {@link Item} instances.
 * <p>
 * Uses the {@link ItemTable} to look up static templates and generates new
 * item instances with specific quantities and properties.
 *
 * @author Matysekxx
 */
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
        final ItemTemplate template = itemTable.getItemTemplate(id);
        if (template == null) {
            throw new IllegalArgumentException("Item template not found: " + id);
        }
        return Item.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .quantity(quantity)
                .maxStack(template.getMaxStack())
                .weight(template.getWeight())
                .type(template.getType())
                .healAmount(template.getHealAmount())
                .damage(template.getDamage())
                .range(template.getRange())
                .price(template.getPrice())
                .effect(template.getEffect())
                .cooldown(template.getCooldown())
                .build();
    }
}
