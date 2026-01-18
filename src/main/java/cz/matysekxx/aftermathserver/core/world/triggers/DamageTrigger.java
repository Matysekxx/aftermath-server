package cz.matysekxx.aftermathserver.core.world.triggers;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class DamageTrigger extends TileTrigger {
    private int damage;
}
