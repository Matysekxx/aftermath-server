package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Inventory;
import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.ItemType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testAddItemStacking() {
        final Inventory inventory = new Inventory(5, 10);
        final Item something = Item.builder()
                .id("something")
                .name("something")
                .description("something")
                .type(ItemType.RESOURCE)
                .quantity(5)
                .maxStack(10).weight(1.0)
                .build();

        boolean result = inventory.addItem(something);
        assertTrue(result);

        inventory.addItem(something.cloneWithQuantity(5));

        assertEquals(1, inventory.getSlots().size());
        assertEquals(10, inventory.getSlots().get(0).getQuantity());
    }

    @Test
    void testWeightLimit() {
        final Inventory inventory = new Inventory(5, 10.0);
        final Item heavySomething = Item.builder()
                .id("something")
                .name("something")
                .description("something")
                .type(ItemType.RESOURCE)
                .quantity(10)
                .maxStack(10)
                .weight(2.0)
                .build();
        boolean result = inventory.addItem(heavySomething);

        assertFalse(result);

    }
}