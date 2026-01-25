package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.LoginOptionsResponse;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class LoginOptionsEventHandler extends GameEventHandler {

    public LoginOptionsEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.SEND_LOGIN_OPTIONS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof LoginOptionsResponse response) {
            networkService.sendLoginOptions(event.targetSessionId(), response);
        }
    }
}