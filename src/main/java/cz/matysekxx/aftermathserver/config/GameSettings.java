package cz.matysekxx.aftermathserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/// Configuration properties for game-wide settings.
///
/// These settings are loaded from the application configuration (e.g., application.yml)
/// using the `game` prefix.
@Data
@Configuration
@ConfigurationProperties(prefix = "game")
public class GameSettings {
    /// The ID of the map where new players start.
    private String startingMapId;
    /// List of map IDs allowed for player spawning.
    private List<String> spawnableMaps;
    /// Default line ID for metro initialization.
    private String lineId;
    /// The rate at which the game engine ticks (in milliseconds).
    private int tickRate;
    /// The default player class assigned to new players.
    private String defaultClass;
    /// A map of player class configurations, keyed by class name.
    private Map<String, PlayerClassConfig> classes;
}