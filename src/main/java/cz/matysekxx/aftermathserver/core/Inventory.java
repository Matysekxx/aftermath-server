package cz.matysekxx.aftermathserver.core;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final int capacity;
    private final Map<Integer, Item> slots = new HashMap<>();

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    public boolean addItem(Item itemToAdd) {
        int amountRemaining = itemToAdd.getQuantity();

        for (Item existingItem : slots.values()) {
            if (existingItem.getId().equals(itemToAdd.getId()) && existingItem.getQuantity() < existingItem.getMaxStack()) {

                final int spaceInStack = existingItem.getMaxStack() - existingItem.getQuantity();
                final int amountToTransfer = Math.min(amountRemaining, spaceInStack);

                existingItem.setQuantity(existingItem.getQuantity() + amountToTransfer);

                amountRemaining -= amountToTransfer;

                if (amountRemaining == 0) return true;
            }
        }
        if (amountRemaining > 0) {
            for (int i = 0; i < capacity; i++) {
                if (!slots.containsKey(i)) {
                    final Item newItem = new Item(itemToAdd.getId(), itemToAdd.getName(), amountRemaining, itemToAdd.getMaxStack());
                    slots.put(i, newItem);
                    return true;
                }
            }
        }
        return amountRemaining == 0;
    }

    public Item removeItem(int slotIndex, int quantityToRemove) {
        if (!slots.containsKey(slotIndex)) return null;

        final Item itemInSlot = slots.get(slotIndex);
        if (itemInSlot.getQuantity() > quantityToRemove) {
            itemInSlot.setQuantity(itemInSlot.getQuantity() - quantityToRemove);
            return new Item(itemInSlot.getId(), itemInSlot.getName(), quantityToRemove, itemInSlot.getMaxStack());

        } else {
            slots.remove(slotIndex);
            return itemInSlot;
        }
    }
}