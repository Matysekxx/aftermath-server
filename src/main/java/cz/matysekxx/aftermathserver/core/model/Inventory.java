package cz.matysekxx.aftermathserver.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Manages a collection of items for a player.
///
/// Handles adding, removing, and stacking items within capacity and weight limits.
public class Inventory {
    private final int capacity;
    private final double maxWeight;
    private final Map<Integer, Item> slots = new HashMap<>();

    /// Creates an inventory with specific limits.
    public Inventory(int capacity, double maxWeight) {
        this.capacity = capacity;
        this.maxWeight = maxWeight;
    }

    /// Removes all items.
    public void clear() {
        slots.clear();
    }

    /// Returns a read-only view of the inventory slots.
    public Map<Integer, Item> getSlots() {
        return Collections.unmodifiableMap(slots);
    }

    /// Calculates the total weight of all items.
    public double getCurrentWeight() {
        return slots.values().stream()
                .mapToDouble(Item::getTotalWeight)
                .sum();
    }

    /// Attempts to add an item to the inventory.
    ///
    /// Tries to stack with existing items first, then finds a free slot.
    /// Checks weight limits.
    ///
    /// @param itemToAdd The item to add.
    /// @return true if the item was fully added, false otherwise.
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

                if (amountRemaining <= 0) return true;
            }
        }

        for (int i = 0; i < capacity && amountRemaining > 0; i++) {
            if (!slots.containsKey(i)) {
                final Item newItem = itemToAdd.cloneWithQuantity(amountRemaining);
                slots.put(i, newItem);
                amountRemaining = 0;
                break;
            }
        }

        return amountRemaining <= 0;
    }

    /// Removes a specific quantity of an item from a slot.
    ///
    /// @param slotIndex        The slot to remove from.
    /// @param quantityToRemove Amount to remove.
    /// @return An Optional containing the removed item (split if necessary), or empty if slot invalid.
    public Optional<Item> removeItem(int slotIndex, int quantityToRemove) {
        if (!slots.containsKey(slotIndex)) return Optional.empty();

        final Item itemInSlot = slots.get(slotIndex);

        if (itemInSlot.getQuantity() > quantityToRemove) {
            itemInSlot.setQuantity(itemInSlot.getQuantity() - quantityToRemove);
            return Optional.of(itemInSlot.cloneWithQuantity(quantityToRemove));
        } else {
            slots.remove(slotIndex);
            return Optional.of(itemInSlot);
        }
    }
}