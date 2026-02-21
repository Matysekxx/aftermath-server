package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.dto.TradeOfferDto;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Logic for handling trading interactions with NPCs.
 *
 * @author Matysekxx
 */
@Component
public class TradeInteractionLogic implements NpcInteractionLogic {
    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        if (target.getShopItems() != null && !target.getShopItems().isEmpty()) {
            final TradeOfferDto offer = new TradeOfferDto(target.getId(), target.getName(), new ArrayList<>(target.getShopItems()));
            return List.of(GameEventFactory.sendTradeUiEvent(offer, player.getId()));
        }
        return List.of(GameEventFactory.sendMessageEvent("This trader has nothing to sell.", player.getId()));
    }

    @Override
    public InteractionType getType() {
        return InteractionType.TRADE;
    }
}
