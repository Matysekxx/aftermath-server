package cz.matysekxx.aftermathserver.core.world;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "game.tiles")
public class TileRegistry {
    private Map<Character, TileType> mapping;
    
    public TileType getType(char c) {
        return mapping.getOrDefault(c, TileType.UNKNOWN);
    }
}
