package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;

@Data
public class Environment {
    private int radiation;
    private boolean oxygen_drain;
    private int darkness_level;
}
