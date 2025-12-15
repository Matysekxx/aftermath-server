package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStateUpdate {
    private int playerX;
    private int playerY;
    private String message;
}
