package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a request to equip an item.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class EquipRequest {
    /** The inventory slot index of the item to equip. */
    private int slotIndex;
}