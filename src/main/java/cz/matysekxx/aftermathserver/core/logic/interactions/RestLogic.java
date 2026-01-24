package cz.matysekxx.aftermathserver.core.logic.interactions;

import cz.matysekxx.aftermathserver.core.model.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("REST")
public class RestLogic implements InteractionLogic{
    @Override
    public Collection<GameEvent> interact(MapObject target, Player player) {
        player.setHp(player.getMaxHp());
        player.setRads(0);
        player.setY(target.getY());
        player.setX(target.getX());
        return List.of(GameEvent.create(EventType.SEND_STATS, player, player.getId(), player.getMapId(), false));
    }
}
