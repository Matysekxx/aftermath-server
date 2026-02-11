package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import cz.matysekxx.aftermathserver.event.GameEventQueue;
import cz.matysekxx.aftermathserver.util.MathUtil;
import cz.matysekxx.aftermathserver.util.Vector2;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hostile behavior that chases and attacks the nearest player.
 *
 * @author Matysekxx
 */
@Component
public class AggressiveBehavior implements Behavior {
    private static final int visionRange = 10;
    private static final int attackCooldown = 1500;
    private final GameEventQueue gameEventQueue;
    private final Map<String, Long> lastAttackTimes = new ConcurrentHashMap<>();

    public AggressiveBehavior(GameEventQueue gameEventQueue) {
        this.gameEventQueue = gameEventQueue;
    }

    /**
     * Updates the NPC state by chasing or attacking the nearest player within vision range.
     *
     * @param npc     The NPC instance to update.
     * @param map     The map data where the NPC is located.
     * @param players Collection of players currently on the same map.
     */
    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
        final Vector2 npcPos = new Vector2(npc.getX(), npc.getY());

        players.stream()
                .filter(p -> p.getLayerIndex() == npc.getLayerIndex())
                .filter(p -> !p.isDead() && MathUtil.getChebyshevDistance(npcPos, new Vector2(p.getX(), p.getY())) <= visionRange)
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
        final long now = System.currentTimeMillis();
        final long lastAttack = lastAttackTimes.getOrDefault(npc.getId(), 0L);

        if (now - lastAttack >= attackCooldown) {
            target.setHp(Math.max(0, target.getHp() - Math.max(1, npc.getDamage())));
            lastAttackTimes.put(npc.getId(), now);

            gameEventQueue.enqueue(GameEventFactory.sendMessageEvent("Byl jsi napaden: " + npc.getName(), target.getId()));
            gameEventQueue.enqueue(GameEventFactory.sendStatsEvent(target));
        }
    }

    private void chase(Npc npc, Player target, GameMapData map) {
        final int dx = Integer.compare(target.getX(), npc.getX());
        final int dy = Integer.compare(target.getY(), npc.getY());

        final int nextX = npc.getX() + dx;
        final int nextY = npc.getY() + dy;

        if (map.isWalkable(nextX, nextY, npc.getLayerIndex())) {
            npc.setX(nextX);
            npc.setY(nextY);
        } else if (dx != 0 && map.isWalkable(npc.getX() + dx, npc.getY(), npc.getLayerIndex())) {
            npc.setX(npc.getX() + dx);
        } else if (dy != 0 && map.isWalkable(npc.getX(), npc.getY() + dy, npc.getLayerIndex())) {
            npc.setY(npc.getY() + dy);
        }
    }
}