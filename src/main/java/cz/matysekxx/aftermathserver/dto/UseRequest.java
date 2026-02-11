package cz.matysekxx.aftermathserver.dto;

import lombok.Data;

/**
 * DTO for a request to use an item from inventory.
 *
 * @author Matysekxx
 */
@Data
public class UseRequest {
    /** The inventory slot index of the item to use. */
    private int slotIndex;
}
