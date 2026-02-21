package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.DialogRegistry;
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
    private final DialogRegistry dialogRegistry;

    public TradeInteractionLogic(DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
    }

    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        final List<GameEvent> events = new ArrayList<>();

        String text = dialogRegistry.getRandomDialog(target.getDialogueId());
        if (text == null) {
            text = "Welcome! Take a look at my wares.";
        }

        if (target.getShopItems() != null && !target.getShopItems().isEmpty()) {
            final TradeOfferDto offer = new TradeOfferDto(target.getId(), target.getName(), new ArrayList<>(target.getShopItems()), text);
            events.add(GameEventFactory.sendTradeUiEvent(offer, player.getId()));
        } else {
            events.add(GameEventFactory.sendDialogEvent(target.getName(), text, player.getId()));
            events.add(GameEventFactory.sendMessageEvent("This trader has nothing to sell.", player.getId()));
        }
        return events;
    }

    @Override
    public InteractionType getType() {
        return InteractionType.TRADE;
    }
}
