package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.model.Item;
import cz.matysekxx.aftermathserver.core.model.ItemFactory;
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
            final Map<Integer, ParsedMapLayer> layers = parseLayoutLayers(mapData.getLayout());
            mapData.setParsedLayers(layers);
            mapData.initializeCache();
            return mapData;
        }
    }

    private Map<Integer, ParsedMapLayer> parseLayoutLayers(Map<Integer, String> layoutFiles) throws IOException {
        if (layoutFiles == null) return Map.of();

        final Map<Integer, ParsedMapLayer> layers = new HashMap<>();
        for (Map.Entry<Integer, String> entry : layoutFiles.entrySet()) {
            layers.put(entry.getKey(), parseFile(entry.getValue()));
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

    private ParsedMapLayer parseFile(String path) throws IOException {
        final ClassPathResource resource = new ClassPathResource(path);
        try (final InputStream is = resource.getInputStream()) {
            final String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return ParsedMapLayer.parse(content, tileRegistry);
        }
    }

    /// Parses a string content into a map layer.
    public ParsedMapLayer parseString(String content) {
        return ParsedMapLayer.parse(content, tileRegistry);
    }
}
