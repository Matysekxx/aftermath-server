package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.util.Direction;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/// Simple behavior where the NPC stays idle or moves randomly.
public class IdleBehavior implements Behavior {
    @Override
    public void update(Npc npc, GameMapData map, Collection<Player> players) {
        if (ThreadLocalRandom.current().nextInt(100) < 10) {
            final Direction dir = Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)];
            final int targetX = npc.getX() + dir.getDx();
            final int targetY = npc.getY() + dir.getDy();

            if (map.isWalkable(npc.getLayerIndex(), targetX, targetY)) {
                npc.setX(targetX);
                npc.setY(targetY);
            }
        }
    }
}