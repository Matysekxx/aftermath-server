package cz.matysekxx.aftermathserver.core.model.entity;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Configuration class holding definitions for all NPCs.
///
/// Loads NPC templates from the application configuration (e.g., YAML)
/// and provides fast lookup by ID.
@Data
@Configuration
@ConfigurationProperties("game.npcs")
public class NpcTable {
    private List<NpcTemplate> definitions;
    private Map<String, NpcTemplate> templatesById = new HashMap<>();

    /// Initializes the lookup map from the loaded list of definitions.
    ///
    /// Executed automatically after dependency injection.
    /// Throws IllegalStateException if duplicate NPC IDs are found.
    @PostConstruct
    public void init() {
        if (definitions != null) {
            for (NpcTemplate npc : definitions) {
                if (templatesById.put(npc.getId(), npc) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
        }
    }

    /// Retrieves an NPC template by its unique ID.
    ///
    /// @param id The ID of the NPC template (e.g., "mutant_rat").
    /// @return The template object, or null if not found.
    public NpcTemplate getTemplate(String id) {
        return templatesById.get(id);
    }
}
