package cz.matysekxx.aftermathserver.core.world.triggers;

import lombok.Data;
import lombok.EqualsAndHashCode;

/// Trigger definition for metro station entry.
@Data
@EqualsAndHashCode(callSuper = true)
public class MetroTrigger extends TileTrigger {
    private String lineId;
}
