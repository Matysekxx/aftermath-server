package cz.matysekxx.aftermathserver.network;

import cz.matysekxx.aftermathserver.dto.TradeOfferDto;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/// Handles `OPEN_TRADE_UI` events by sending the trade offer to the client.
@Component
public class OpenTradeUIEventHandler extends GameEventHandler {

    /// Constructs the OpenTradeUIEventHandler.
    public OpenTradeUIEventHandler(@Lazy NetworkService networkService) {
        super(networkService);
    }

    @Override
    public EventType getType() {
        return EventType.OPEN_TRADE_UI;
    }

    @Override
    public void handleEvent(GameEvent event) {
        if (event.payload() instanceof TradeOfferDto offer) {
            networkService.sendTradeOffer(event.targetSessionId(), offer);
        }
    }
}