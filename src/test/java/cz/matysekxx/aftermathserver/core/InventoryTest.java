package cz.matysekxx.aftermathserver.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    @Test
    void testAddItemStacking() {
        final Inventory inventory = new Inventory(5, 10);
        final Item something = new Item(
                "something",
                "something",
                "...",
                "s",
                5,
                10,
                1.0
        );

        boolean result = inventory.addItem(something);
        assertTrue(result);

        inventory.addItem(something.cloneWithQuantity(5));

        assertEquals(1, inventory.getSlots().size());
        assertEquals(10, inventory.getSlots().get(0).getQuantity());
    }

    @Test
    void testWeightLimit() {
        final Inventory inventory = new Inventory(5, 10.0);
        final Item  heavySomething = new Item("heavy something", "heavy something", "...", "E", 1, 1, 11.0);
        boolean result = inventory.addItem(heavySomething);

        assertFalse(result);

    }
}