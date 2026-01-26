package cz.matysekxx.aftermathserver.dto;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
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

    public static NpcDto fromEntity(Npc npc) {
        return new NpcDto(
                npc.getId(),
                npc.getName(),
                npc.getType(),
                npc.getX(),
                npc.getY(),
                npc.getHp(),
                npc.getMaxHp(),
                npc.isAggressive()
        );
    }
}