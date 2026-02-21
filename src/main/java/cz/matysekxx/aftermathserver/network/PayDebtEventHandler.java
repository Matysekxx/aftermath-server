package cz.matysekxx.aftermathserver.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.matysekxx.aftermathserver.core.EconomyService;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.dto.PayDebtRequest;
import cz.matysekxx.aftermathserver.event.EventType;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PayDebtEventHandler extends GameEventHandler {
    private final EconomyService economyService;
    private final GameEngine gameEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PayDebtEventHandler(@Lazy NetworkService networkService, EconomyService economyService, GameEngine gameEngine) {
        super(networkService);
        this.economyService = economyService;
        this.gameEngine = gameEngine;
    }

    @Override
    public void handleEvent(GameEvent event) {
        try {
            final PayDebtRequest request = objectMapper.convertValue(event.payload(), PayDebtRequest.class);
            final Optional<Player> maybePlayer = gameEngine.getMaybePlayerById(event.targetSessionId());

            maybePlayer.ifPresent(player -> {
                if (player.getDebt() > 0) {
                    economyService.payPersonalDebt(player, request.getAmount());
                } else {
                    economyService.contributeToGlobalDebt(player, request.getAmount());
                }
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    public EventType getType() {
        return EventType.PAY_DEBT;
    }
}