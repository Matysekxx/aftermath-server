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
}
