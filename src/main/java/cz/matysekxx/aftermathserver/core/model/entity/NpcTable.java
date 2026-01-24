package cz.matysekxx.aftermathserver.core.model.entity;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("game.npcs")
public class NpcTable {
    private List<NpcTemplate> definitions;
    private Map<String, NpcTemplate> templatesById = new HashMap<>();

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

    public NpcTemplate getTemplate(String id) {
        return templatesById.get(id);
    }
}
