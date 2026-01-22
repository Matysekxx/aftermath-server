package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Manages the lifecycle and access to all game maps.
///
/// Loads maps on startup and provides methods to query map data and tile properties.
@Service
@Slf4j
public class WorldManager {

    private final Map<String, GameMapData> maps = new ConcurrentHashMap<>();
    private final MapParser mapParser;

    public WorldManager(MapParser mapParser) {
        this.mapParser = mapParser;
    }

    /// Loads all maps from the classpath assets.
    @PostConstruct
    public void loadMaps() {
        log.info("Loading maps...");

        try {
            final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final Resource[] resources = resolver.getResources("classpath:assets/*.json");

            for (Resource resource : resources) {
                try {
                    final GameMapData map = mapParser.loadMap("assets/" + resource.getFilename());
                    maps.put(map.getId(), map);

                    log.info("Loaded map: {} ({} layers)", map.getId(), map.getLayerCount());
                } catch (Exception e) {
                    log.error("Error loading {}: {}", resource.getFilename(), e.getMessage());
                }
            }

            log.info("Total maps loaded: {}", maps.size());
        } catch (IOException e) {
            log.error("Error scanning maps: {}", e.getMessage());
        }
    }

    /// Retrieves a map by its ID.
    public GameMapData getMap(String mapId) {
        if (maps.containsKey(mapId)) return  maps.get(mapId);
        return null;
    }

    /// Checks if a map exists.
    public boolean containsMap(String mapId) {
        return maps.containsKey(mapId);
    }

    /// Gets the tile type at specific coordinates.
    public TileType getTileAt(String mapId, int layer, int x, int y) {
        GameMapData map = maps.get(mapId);
        if (map == null) return TileType.VOID;

        ParsedMapLayer parsedLayer = map.getLayer(layer);
        if (parsedLayer == null) return TileType.VOID;

        return parsedLayer.getTileAt(x, y);
    }

    /// Checks if a specific coordinate is walkable.
    public boolean isWalkable(String mapId, int layer, int x, int y) {
        return getTileAt(mapId, layer, x, y).isWalkable();
    }
}
