package cz.matysekxx.aftermathserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.dto.LoginOptionsResponse;
import cz.matysekxx.aftermathserver.dto.SpawnPointInfo;
import cz.matysekxx.aftermathserver.dto.WebSocketResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SerializationTest {

    @Test
    public void testLoginOptionsSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        List<String> classes = List.of("scavenger", "soldier");
        List<SpawnPointInfo> maps = List.of(new SpawnPointInfo("map1", "Map 1"));
        
        LoginOptionsResponse loginOptions = new LoginOptionsResponse(classes, maps);
        WebSocketResponse response = WebSocketResponse.of("LOGIN_OPTIONS", loginOptions);
        
        String json = mapper.writeValueAsString(response);
        
        System.out.println("JSON: " + json);
        
        assertTrue(json.contains("\"type\":\"LOGIN_OPTIONS\""));
        assertTrue(json.contains("\"payload\":{"));
        assertTrue(json.contains("\"classes\":[\"scavenger\",\"soldier\"]"));
        assertTrue(json.contains("\"maps\":[{\"mapId\":\"map1\",\"mapName\":\"Map 1\"}]"));
    }
}
