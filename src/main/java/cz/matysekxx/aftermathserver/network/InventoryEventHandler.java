package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_INVENTORY} events by sending the player's inventory contents to the client.
 *
 * @author Matysekxx
 */
@Component
public class InventoryEventHandler extends GameEventHandler {

    public InventoryEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_INVENTORY;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Player player) networkService.sendInventory(player);
    }
}