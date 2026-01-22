package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

/// Defines environmental settings for a map.
@Data
public class Environment {
    /// Radiation level per tick.
    private int radiation;
    /// Visual darkness level.
    private int darkness_level;
}
