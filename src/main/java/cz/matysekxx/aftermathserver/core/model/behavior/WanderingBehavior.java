package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.world.GameMapData;
import cz.matysekxx.aftermathserver.util.Direction;
import cz.matysekxx.aftermathserver.util.Vector2;

import java.util.concurrent.ThreadLocalRandom;

public class WanderingBehavior implements Behavior{
    @Override
    public void performAction(Npc npc, GameMapData gameMapData) {
        final Vector2 currPos = new Vector2(npc.getX(), npc.getY());
        final Direction dir = Direction.values()[ThreadLocalRandom.current().nextInt(Direction.values().length)];
        final Vector2 newPos = new Vector2(
                currPos.x() + dir.getDx(),
                currPos.y() + dir.getDy()
        );
        if (gameMapData.isWalkable(newPos.x(), newPos.y(), npc.getLayerIndex())) {
            npc.setX(newPos.x());
            npc.setY(newPos.y());
        }
    }
}
