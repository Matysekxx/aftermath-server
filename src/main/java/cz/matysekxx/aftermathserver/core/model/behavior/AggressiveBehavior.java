package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Vector2;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

/// Hostile behavior that chases and attacks the nearest player.
public class AggressiveBehavior implements Behavior {
    private final int visionRange = 10;

    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
        final Vector2 npcPos = new Vector2(npc.getX(), npc.getY());

        players.stream()
                .filter(p -> !p.isDead())
                .filter(p -> MathUtil.getChebyshevDistance(npcPos, new Vector2(p.getX(), p.getY())) <= visionRange)
                .min(Comparator.comparingInt(p -> MathUtil.getChebyshevDistance(npcPos, new Vector2(p.getX(), p.getY()))))
                .ifPresent(target -> {
                    final int distance = MathUtil.getChebyshevDistance(npcPos, new Vector2(target.getX(), target.getY()));
                    if (distance <= 1) {
                        attack(npc, target);
                    } else {
                        chase(npc, target, map);
                    }
                });
    }

    private void attack(Npc npc, Player target) {
    }

    private void chase(Npc npc, Player target, GameMapData map) {
    }
}