package cz.matysekxx.aftermathserver.core.world.triggers;

import lombok.Data;
import lombok.EqualsAndHashCode;

/// Trigger definition for teleportation tiles.
@Data
@EqualsAndHashCode(callSuper = true)
public class TeleportTrigger extends TileTrigger {
    private int targetX;
    private int targetY;
    private int targetLayer;

    public TeleportTrigger(int targetX, int targetY, int targetLayer) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetLayer = targetLayer;
    }
}
