package cz.matysekxx.aftermathserver.core.world;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WorldManager {

    private final Map<String, GameMapData> maps = new ConcurrentHashMap<>();
    private final MapParser mapParser;

    public WorldManager(MapParser mapParser) {
        this.mapParser = mapParser;
    }

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

    public GameMapData getMap(String mapId) {
        if (maps.containsKey(mapId)) return  maps.get(mapId);
        throw new IllegalArgumentException("Invalid map ID: " + mapId);
    }

    public TileType getTileAt(String mapId, int layer, int x, int y) {
        GameMapData map = maps.get(mapId);
        if (map == null) return TileType.VOID;

        ParsedMapLayer parsedLayer = map.getLayer(layer);
        if (parsedLayer == null) return TileType.VOID;

        return parsedLayer.getTileAt(x, y);
    }

    public boolean isWalkable(String mapId, int layer, int x, int y) {
        return getTileAt(mapId, layer, x, y).isWalkable();
    }
}
