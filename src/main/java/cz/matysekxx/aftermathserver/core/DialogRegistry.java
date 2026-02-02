package cz.matysekxx.aftermathserver.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/// Registry for managing NPC dialogs loaded from configuration.
///
/// Provides access to dialog lines based on NPC keys.
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "game.dialog")
public class DialogRegistry {
    private Map<String, List<String>> dialogMap = new HashMap<>();

    /// Retrieves all dialog lines associated with a specific key.
    ///
    /// @param key The dialog key (e.g., NPC ID).
    /// @return A list of dialog strings, or null if not found.
    public List<String> getDialogs(String key) {
        return dialogMap.get(key);
    }

    /// Retrieves a random dialog line for a specific key.
    ///
    /// @param key The dialog key.
    /// @return A random dialog string, or null if not found or empty.
    public String getRandomDialog(String key) {
        final List<String> dialogs = dialogMap.get(key);
        if (dialogs == null || dialogs.isEmpty()) return null;
        return dialogs.get(ThreadLocalRandom.current().nextInt(0, dialogs.size()));
    }
}
