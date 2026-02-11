package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a buy request from a player to an NPC.
 *
 * @author Matysekxx
 */
@Data
@NoArgsConstructor
public class BuyRequest {
    /** The ID of the NPC trader. */
    private String npcId;
    /** The index of the item in the NPC's shop list. */
    private int itemIndex;
}