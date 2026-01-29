package cz.matysekxx.aftermathserver.dto;

import lombok.Data;

@Data
public class AttackRequest {
    private String targetId;
    private Integer weaponIndex;
}
