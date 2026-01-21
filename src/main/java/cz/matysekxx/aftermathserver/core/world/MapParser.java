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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public GameMapData loadMap(String jsonPath) throws IOException {
        final ClassPathResource resource = new ClassPathResource(jsonPath);
        try (final InputStream is = resource.getInputStream()) {
            final GameMapData mapData = objectMapper.readValue(is, GameMapData.class);
            processMapObjects(mapData);
            final List<ParsedMapLayer> layers = parseLayoutLayers(mapData.getLayout());
            mapData.setParsedLayers(layers);
            mapData.initializeCache();
            return mapData;
        }
    }

    private List<ParsedMapLayer> parseLayoutLayers(List<String> layoutFiles) throws IOException {
        if (layoutFiles == null) return Collections.emptyList();

        final List<ParsedMapLayer> layers = new ArrayList<>();
        for (String file : layoutFiles) {
            layers.add(parseFile(file));
        }
        return Collections.unmodifiableList(layers);
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

    public ParsedMapLayer parseString(String content) {
        return ParsedMapLayer.parse(content, tileRegistry);
    }
}
