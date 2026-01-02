package cz.matysekxx.aftermathserver.core.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class MapParser {
    
    private final TileRegistry tileRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public MapParser(TileRegistry tileRegistry) {
        this.tileRegistry = tileRegistry;
    }

    public GameMapData loadMap(String jsonPath) throws IOException {
        ClassPathResource resource = new ClassPathResource(jsonPath);
        
        try (InputStream is = resource.getInputStream()) {
            final GameMapData mapData = objectMapper.readValue(is, GameMapData.class);

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
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return ParsedMapLayer.parse(content, tileRegistry);
        }
    }

    public ParsedMapLayer parseString(String content) {
        return ParsedMapLayer.parse(content, tileRegistry);
    }
}
