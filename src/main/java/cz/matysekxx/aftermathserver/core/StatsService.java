package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.Environment;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import org.springframework.stereotype.Service;

@Service
public class StatsService {
    private final WorldManager worldManager;

    public StatsService(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public boolean applyStats(Player player) {
        final GameMapData map = worldManager.getMap(player.getMapId());
        final Environment env = map.getEnvironment();
        return switch (map.getType()) {
            case MapType.HAZARD_ZONE -> applyRadiation(player, env);
            case MapType.SAFE_ZONE -> applyRegeneration(player);
        };
    }

    private boolean applyRegeneration(Player player) {
        if (player.getHp() < player.getMaxHp()) {
            player.setHp(player.getHp() + 1);
            return true;
        }
        if (player.getRads() > 0) {
            player.setRads(Math.max(0, player.getRads() - 5));
            return true;
        }
        return false;
    }

    private boolean applyRadiation(Player player, Environment env) {
        if (env.getRadiation() > 0) {
            player.setRads(player.getRads() + env.getRadiation());
            if (player.getRads() > player.getRadsLimit()) {
                player.setHp(player.getHp() - 1);
                return true;
            }
        }
        return false;
    }
}
