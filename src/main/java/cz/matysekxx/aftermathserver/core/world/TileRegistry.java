package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class TileRegistry {
    
    private final Map<Character, TileType> charToType = new HashMap<>();
    
    @PostConstruct
    public void init() {
        register('#', TileType.WALL);
        register('.', TileType.FLOOR);
        register(' ', TileType.EMPTY);
        register('|', TileType.WALL);
        register('/', TileType.WALL);
        register('\\', TileType.WALL);
        register('─', TileType.WALL);

        register('│', TileType.WALL);
        register('─',  TileType.WALL);
        register('┌', TileType.WALL);
        register('└', TileType.WALL);
        register('┐', TileType.WALL);
        register('┘', TileType.WALL);



        register('=', TileType.DOOR);
        register('B', TileType.BED);
        register('V', TileType.ELEVATOR);

        register('C', TileType.COMPUTER);
        register('R', TileType.RADIO);
        register('S', TileType.SERVER);

        register('A', TileType.ARMORY);
        register('W', TileType.WEAPON);
        register('F', TileType.FOOD);
        register('O', TileType.OVEN);
    }
    
    public void register(char c, TileType type) {
        charToType.put(c, type);
    }

    public void registerFromLegend(Map<String, String> legend) {
        if (legend == null) return;
        for (Map.Entry<String, String> entry : legend.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key.length() == 1) {
                try {
                    register(key.charAt(0), TileType.valueOf(value.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    register(key.charAt(0), TileType.UNKNOWN);
                }
            }
        }
    }
    
    public TileType getType(char c) {
        return charToType.getOrDefault(c, TileType.UNKNOWN);
    }
}
