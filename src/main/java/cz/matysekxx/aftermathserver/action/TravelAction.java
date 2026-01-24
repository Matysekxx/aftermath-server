package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.core.model.entity.State;
import cz.matysekxx.aftermathserver.dto.TravelRequest;
import org.springframework.stereotype.Component;

/// Handles metro travel requests.
///
/// Triggered by the `TRAVEL` command from the client's metro UI.
/// Validates if the player is in the correct state and initiates the travel process via MetroService.
@Component("TRAVEL")
public class TravelAction extends Action {
    private final MetroService metroService;

    protected TravelAction(GameEngine gameEngine, MetroService metroService) {
        super(gameEngine);
        this.metroService = metroService;
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final TravelRequest travelRequest = objectMapper.convertValue(payload, TravelRequest.class);
        final Player player = gameEngine.getPlayerById(sessionId);
        if (player.getState() == State.TRAVELLING) {
            metroService.startTravel(player, travelRequest.getMapId(), travelRequest.getLineId());
        }
    }
}
