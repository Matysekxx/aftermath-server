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
            if (mapData.getObjects() != null) {
                for (MapObject object : mapData.getObjects()) {
                    if (object.getItems() != null && !object.getItems().isEmpty()) {
                        final List<Item> fullItems = new ArrayList<>();
                        for (Item thinItem : object.getItems()) {
                            fullItems.add(itemFactory.createItem(thinItem.getId(), thinItem.getQuantity()));
                        }
                        object.setItems(fullItems);
                    }
                    
                }
            }
            mapData.initializeCache();

            final List<ParsedMapLayer> layers = new ArrayList<>();
            if (mapData.getLayout() != null) {
                for (String file : mapData.getLayout()) {
                    layers.add(parseFile("assets/" + file));
                }
            }
            mapData.setParsedLayers(layers);
            
            return mapData;
        }
    }

    public ParsedMapLayer parseFile(String path) throws IOException {
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
