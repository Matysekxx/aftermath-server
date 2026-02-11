package cz.matysekxx.aftermathserver.core.model.entity;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class holding definitions for all NPCs.
 * <p>
 * Loads NPC templates from the application configuration (e.g., YAML)
 * and provides fast lookup by ID.
 *
 * @author Matysekxx
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties("game.npcs")
public class NpcTable {
    /** List of mutant NPC templates. */
    private List<NpcTemplate> mutantNpcs;
    /** List of trader NPC templates. */
    private List<NpcTemplate> traderNpcs;
    /** List of story-related NPC templates. */
    private List<NpcTemplate> storyNpcs;
    /** Lookup map for NPC templates by their ID. */
    private Map<String, NpcTemplate> templatesById = new HashMap<>();

    /**
     * Initializes the lookup map from the loaded lists of definitions.
     * <p>
     * Executed automatically after dependency injection.
     */
    @PostConstruct
    public void init() {
        addDefinitions(mutantNpcs);
        addDefinitions(traderNpcs);
        addDefinitions(storyNpcs);
    }

    private void addDefinitions(List<NpcTemplate> list) {
        if (list != null) {
            for (NpcTemplate npc : list) {
                if (templatesById.put(npc.getId(), npc) != null) {
                    log.warn("Duplicate NPC ID found: {}. Overriding.", npc.getId());
                }
                log.info("npc {} loaded", npc.getId());
            }
        }
    }

    /**
     * Retrieves an NPC template by its unique ID.
     *
     * @param id The ID of the NPC template (e.g., "mutant_rat").
     * @return The template object, or null if not found.
     */
    public NpcTemplate getTemplate(String id) {
        return templatesById.get(id);
    }

    /**
     * Returns all loaded NPC templates.
     *
     * @return A list of all NPC templates.
     */
    public List<NpcTemplate> getDefinitions() {
        return new ArrayList<>(templatesById.values());
    }
}
