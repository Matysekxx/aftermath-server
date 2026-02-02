package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeOfferDto {
    private String npcId;
    private String npcName;
    private List<Item> items;
}