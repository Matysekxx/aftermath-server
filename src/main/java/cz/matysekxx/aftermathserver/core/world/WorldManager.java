package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorldManager {

    private final Map<String, GameMapData> mapCache = new HashMap<>();

    private final Map<String, String> playerLocations = new HashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void loadMaps() {

    }

    public final GameMapData getMap(String mapId) {
        return mapCache.get(mapId);
    }

    public final GameMapData getStartingMap() {
        return null;
    }
}
