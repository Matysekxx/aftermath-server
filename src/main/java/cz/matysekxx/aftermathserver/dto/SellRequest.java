package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a sell request from a player to an NPC.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class SellRequest {
    /**
     * The ID of the NPC trader.
     */
    private String npcId;
    /**
     * The inventory slot index of the item to sell.
     */
    private int slotIndex;
}