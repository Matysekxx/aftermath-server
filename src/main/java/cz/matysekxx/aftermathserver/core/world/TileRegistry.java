package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/// Registry mapping characters in map files to `TileType` definitions.
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "game.tiles")
public class TileRegistry {
    private Collection<TileDefinition> definitions;
    private Map<Character, TileType> mapping = new HashMap<>();

    @PostConstruct
    public void init() {
        if (definitions != null) {
            for (TileDefinition def : definitions)
                mapping.put(def.getSymbol(), def.getType());
        }
    }

    /// Looks up the TileType for a given character symbol.
    public TileType getType(char c) {
        return mapping.getOrDefault(c, TileType.UNKNOWN);
    }

    @Data
    public static class TileDefinition {
        private char symbol;
        private TileType type;
    }
}
