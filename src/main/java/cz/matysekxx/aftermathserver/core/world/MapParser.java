package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.item.Item;
import cz.matysekxx.aftermathserver.core.model.item.ItemFactory;
import cz.matysekxx.aftermathserver.core.world.triggers.Link;
import cz.matysekxx.aftermathserver.core.world.triggers.TeleportTrigger;
import cz.matysekxx.aftermathserver.util.Vector3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/// Service for parsing map files.
///
/// Handles loading JSON metadata and parsing text-based layout files.
@Service
@Slf4j
public class MapParser {
    private final TileRegistry tileRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ItemFactory itemFactory;

    public MapParser(TileRegistry tileRegistry, ItemFactory itemFactory) {
        this.tileRegistry = tileRegistry;
        this.itemFactory = itemFactory;
    }

    /// Loads a map from a JSON file path.
    public GameMapData loadMap(String jsonPath) throws IOException {
        final ClassPathResource resource = new ClassPathResource(jsonPath);
        try (final InputStream is = resource.getInputStream()) {
            final GameMapData mapData = objectMapper.readValue(is, GameMapData.class);
            processMapObjects(mapData);
            final Map<Integer, ParsedMapLayer> layers = parseLayoutLayers(mapData);
            mapData.setParsedLayers(layers);

            processLinks(mapData, layers);

            mapData.initializeCache();
            return mapData;
        }
    }

    private void processLinks(GameMapData mapData, Map<Integer, ParsedMapLayer> layers) {
        if (mapData.getLinks() == null || mapData.getLinks().isEmpty()) return;
        final Map<String, List<Vector3>> globalMarkers = new HashMap<>();
        for (ParsedMapLayer layer : layers.values()) {
            layer.getMarkers().forEach((key, value) ->
                    globalMarkers.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value)
            );
        }
        for (Link link : mapData.getLinks()) {
            final List<Vector3> sources = globalMarkers.get(link.from());
            final List<Vector3> destinations = globalMarkers.get(link.to());
            if (sources == null || destinations == null || destinations.isEmpty()) {
                log.warn("Invalid link in map {}: {} -> {}", mapData.getId(), link.from(), link.to());
                continue;
            }
            final Vector3 dest = destinations.getFirst();
            for (Vector3 src : sources) {
                final TeleportTrigger trigger = new TeleportTrigger(dest.x(), dest.y(), dest.z());
                mapData.getDynamicTriggers().put(src, trigger);
            }
        }
    }

    private Map<Integer, ParsedMapLayer> parseLayoutLayers(GameMapData mapData) throws IOException {
        Map<Integer, String> layoutFiles = mapData.getLayout();
        if (layoutFiles == null) return Map.of();

        final Map<Integer, ParsedMapLayer> layers = new HashMap<>();
        for (Map.Entry<Integer, String> entry : layoutFiles.entrySet()) {
            layers.put(entry.getKey(), parseFile(entry, mapData));
        }
        return Collections.unmodifiableMap(layers);
    }

    private void processMapObjects(GameMapData mapData) {
        if (mapData.getObjects() == null) return;

        for (MapObject object : mapData.getObjects()) {
            if (object.getItems() == null || object.getItems().isEmpty()) continue;
            final List<Item> fullItems = new ArrayList<>();
            for (Item item : object.getItems()) {
                Item itemFactoryItem = itemFactory.createItem(item.getId(), item.getQuantity());
                fullItems.add(itemFactoryItem);
            }
            object.setItems(new ArrayList<>(fullItems));
        }
    }

    private ParsedMapLayer parseFile(Map.Entry<Integer, String> entry, GameMapData mapData) throws IOException {
        final ClassPathResource resource = new ClassPathResource(entry.getValue());
        try (final InputStream is = resource.getInputStream()) {
            final String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return ParsedMapLayer.parse(content, tileRegistry, entry.getKey(), mapData);
        }
    }

    /// Parses a string content into a map layer.
    public ParsedMapLayer parseString(String content) {
        return ParsedMapLayer.parse(content, tileRegistry, 0, null);
    }
}