package cz.matysekxx.aftermathserver.core;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.Environment;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.core.world.MapType;
import cz.matysekxx.aftermathserver.core.world.WorldManager;
import org.springframework.stereotype.Service;

/// Service responsible for managing and applying periodic changes to player statistics.
///
/// This service handles environmental effects such as radiation damage in hazard zones
/// and health regeneration in safe zones.
@Service
public class StatsService {
    private final WorldManager worldManager;

    public StatsService(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /// Applies environmental effects to a player based on the current map type.
    ///
    /// @param player The player to update.
    /// @return true if any statistics were changed, false otherwise.
    public boolean applyStats(Player player) {
        final GameMapData map = worldManager.getMap(player.getMapId());
        final Environment env = map.getEnvironment();
        return switch (map.getType()) {
            case MapType.HAZARD_ZONE -> applyRadiation(player, env);
            case MapType.SAFE_ZONE -> applyRegeneration(player);
        };
    }

    /// Restores health and reduces radiation for players in safe zones.
    ///
    /// @param player The player to regenerate.
    /// @return true if health or radiation levels were modified.
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

    /// Increases radiation levels and applies damage if the radiation limit is exceeded.
    ///
    /// @param player The player affected by radiation.
    /// @param env The environmental settings of the current map.
    /// @return true if radiation increased or health decreased.
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
