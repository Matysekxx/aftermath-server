package cz.matysekxx.aftermathserver.core.logic.interactions.object;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.world.MapObject;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Handles reading interactions.
 * <p>
 * Sends the description of an object (e.g., a sign or note) to the player.
 *
 * @author Matysekxx
 */
@Component("READ")
public class ReadLogicObject implements ObjectInteractionLogic {
    /**
     * @param target The map object being interacted with.
     * @param player The player performing the interaction.
     * @return A collection of GameEvents resulting from the interaction.
     */
    @Override
    public Collection<GameEvent> interact(MapObject target, Player player) {
        return List.of(GameEventFactory.sendMessageEvent(target.getDescription(), player.getId()));
    }
}