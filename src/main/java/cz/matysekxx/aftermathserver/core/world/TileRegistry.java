package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry mapping characters in map files to {@link TileType} definitions.
 *
 * @author Matysekxx
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "game.tiles")
public class TileRegistry {
    private List<TileDefinition> definitions;
    private Map<Character, TileType> mapping = new HashMap<>();

    @PostConstruct
    public void init() {
        if (definitions != null) {
            for (TileDefinition def : definitions)
                mapping.put(def.getSymbol(), def.getType());
        }
    }

    /**
     * Looks up the TileType for a given character symbol.
     * @param c The character symbol.
     * @return The corresponding TileType.
     */
    public TileType getType(char c) {
        return mapping.getOrDefault(c, TileType.UNKNOWN);
    }

    @Data
    public static class TileDefinition {
        private char symbol;
        private TileType type;
    }
}
