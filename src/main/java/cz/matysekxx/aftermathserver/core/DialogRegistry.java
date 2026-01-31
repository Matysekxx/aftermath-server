package cz.matysekxx.aftermathserver.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "game.dialog")
public class DialogRegistry {
    private Map<String, List<String>> dialogMap = new HashMap<>();

    public List<String> getDialogs(String key) {
        return dialogMap.get(key);
    }

    public String getRandomDialog(String key) {
        if (dialogMap.get(key) == null) return null;
        return  dialogMap.get(key).get(ThreadLocalRandom.current().nextInt(0, dialogMap.get(key).size()));
    }
}
