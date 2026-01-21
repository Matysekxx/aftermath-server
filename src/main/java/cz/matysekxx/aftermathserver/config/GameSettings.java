package cz.matysekxx.aftermathserver.config;

import cz.matysekxx.aftermathserver.util.Coordination;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "game")
public class GameSettings {
    private String startingMapId;
    private int tickRate;

    private String defaultClass;
    private Map<String, PlayerClassConfig> classes;

    @Data
    public static class PlayerClassConfig {
        private int maxHp;
        private int inventoryCapacity;
        private double maxWeight;
        private int radsLimit;
    }
}