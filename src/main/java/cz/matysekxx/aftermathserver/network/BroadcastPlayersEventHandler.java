package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.OtherPlayerDto;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class BroadcastPlayersEventHandler extends GameEventHandler {

    public BroadcastPlayersEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.BROADCAST_PLAYERS;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof Collection<?> players) {
            @SuppressWarnings("unchecked")
            final List<OtherPlayerDto> otherPlayers = (List<OtherPlayerDto>) players;
            networkService.broadcastPlayers(otherPlayers, event.mapId());
        }
    }
}
