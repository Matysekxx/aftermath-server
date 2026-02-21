package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.DialogResponse;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Handles {@code SEND_DIALOG} events by sending a dialog message to the client.
 *
 * @author Matysekxx
 */
@Component
public class DialogEventHandler extends GameEventHandler {

    protected DialogEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_DIALOG;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof DialogResponse dialogResponse) {
            networkService.sendDialog(event.targetSessionId(), dialogResponse);
        }
    }
}
