package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
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
        register('├', TileType.WALL);
        register('┤', TileType.WALL);
        register('┴', TileType.WALL);
        register('┬', TileType.WALL);
        register('┼', TileType.WALL);



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
        log.info("Registry loaded");
    }
    
    public void register(char c, TileType type) {
        charToType.put(c, type);
    }
    
    public TileType getType(char c) {
        return charToType.getOrDefault(c, TileType.UNKNOWN);
    }
}
