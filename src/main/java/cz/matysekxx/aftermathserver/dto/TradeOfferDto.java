package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing a trade offer from an NPC.
 * Sent to the client to open the trade UI.
 *
 * @author Matysekxx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeOfferDto {
    /**
     * The ID of the NPC trader.
     */
    private String npcId;
    /**
     * The name of the NPC trader.
     */
    private String npcName;
    /**
     * The list of items available for purchase.
     */
    private List<Item> items;
}