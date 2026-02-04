package cz.matysekxx.aftermathserver.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SellRequest {
    private String npcId;
    private int slotIndex;
}