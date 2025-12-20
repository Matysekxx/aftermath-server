package cz.matysekxx.aftermathserver.core.world;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WorldManager {

    private final Map<String, GameMapData> mapCache = new HashMap<>();

    private final Map<String, String> playerLocations = new HashMap<>();
}
