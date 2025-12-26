package cz.matysekxx.aftermathserver.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final int capacity;
    private final double maxWeight;
    private final Map<Integer, Item> slots = new HashMap<>();

    public Inventory(int capacity, double maxWeight) {
        this.capacity = capacity;
        this.maxWeight = maxWeight;
    }

    public void clear() {
        slots.clear();
    }

    public Map<Integer, Item> getSlots() {
        return Collections.unmodifiableMap(slots);
    }

    public double getCurrentWeight() {
        return slots.values().stream()
                .mapToDouble(Item::getTotalWeight)
                .sum();
    }

    public boolean addItem(Item itemToAdd) {
        if (getCurrentWeight() + itemToAdd.getTotalWeight() > maxWeight) {
            return false;
        }

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
                    final Item newItem = itemToAdd.cloneWithQuantity(amountRemaining);
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
            return itemInSlot.cloneWithQuantity(quantityToRemove);
        } else {
            slots.remove(slotIndex);
            return itemInSlot;
        }
    }
}