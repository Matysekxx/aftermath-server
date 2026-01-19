package cz.matysekxx.aftermathserver.core.world.triggers;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class HealTrigger extends TileTrigger {
    private int healAmount;
}
