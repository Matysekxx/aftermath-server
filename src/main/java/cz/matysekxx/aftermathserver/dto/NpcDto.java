package cz.matysekxx.aftermathserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NpcDto {
    private String id;
    private String name;
    private String type;
    private int x;
    private int y;
    private int hp;
    private int maxHp;
    private boolean aggressive;
}