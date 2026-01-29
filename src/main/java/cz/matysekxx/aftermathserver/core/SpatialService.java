package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import cz.matysekxx.aftermathserver.util.Quadtree;
import cz.matysekxx.aftermathserver.util.Rectangle;
import cz.matysekxx.aftermathserver.util.Spatial;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class SpatialService {
    private final Map<String, Map<Integer, Quadtree<Spatial>>> spatialIndices = new ConcurrentHashMap<>();
    private final WorldManager worldManager;

    public SpatialService(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void rebuildIndex(String mapId, List<? extends Spatial> entities) {
        final GameMapData mapData = worldManager.getMap(mapId);
        if (mapData == null) return;

        final Map<Integer, Quadtree<Spatial>> layerMap = new ConcurrentHashMap<>();

        for (Spatial entity : entities) {
            final int z = entity.getLayerIndex();
            layerMap.computeIfAbsent(z, new Function<>() {
                @Override
                public Quadtree<Spatial> apply(Integer k) {
                    return new Quadtree<>(0, Rectangle.of(0, 0, 1000, 1000));
                }
            }).insert(entity);
        }
        spatialIndices.put(mapId, layerMap);
    }

    public List<Spatial> getNearby(String mapId, Spatial center) {
        final Map<Integer, Quadtree<Spatial>> layerMap = spatialIndices.get(mapId);
        if (layerMap == null) return List.of();
        final Quadtree<Spatial> quadtree = layerMap.get(center.getLayerIndex());
        if (quadtree == null) return List.of();
        return quadtree.retrieve(List.of(), center);
    }
}