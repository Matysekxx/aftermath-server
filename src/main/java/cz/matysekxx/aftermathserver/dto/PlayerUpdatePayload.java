package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerUpdatePayload {
    private String playerId;
    private int x;
    private int y;
    private int z;
}