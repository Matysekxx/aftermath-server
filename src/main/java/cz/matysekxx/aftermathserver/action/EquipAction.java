package cz.matysekxx.aftermathserver.action;

import com.fasterxml.jackson.databind.JsonNode;
import cz.matysekxx.aftermathserver.core.GameEngine;
import cz.matysekxx.aftermathserver.dto.EquipRequest;
import org.springframework.stereotype.Component;

@Component("EQUIP")
public class EquipAction extends Action {

    public EquipAction(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void execute(String sessionId, JsonNode payload) {
        final EquipRequest request = objectMapper.convertValue(payload, EquipRequest.class);
        gameEngine.processEquip(sessionId, request);
    }
}