package cz.matysekxx.aftermathserver.core.world.triggers;

import lombok.Data;
import lombok.EqualsAndHashCode;

/// Trigger definition for damaging tiles.
@Data
@EqualsAndHashCode(callSuper = true)
public class DamageTrigger extends TileTrigger {
    private int damage;
}
