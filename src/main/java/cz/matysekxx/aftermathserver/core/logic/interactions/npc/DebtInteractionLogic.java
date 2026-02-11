package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.EconomyService;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Logic for handling debt payment interactions with NPCs.
 *
 * @author Matysekxx
 */
@Slf4j
@Component
public class DebtInteractionLogic implements NpcInteractionLogic {
    private final EconomyService economyService;

    public DebtInteractionLogic(EconomyService economyService) {
        this.economyService = economyService;
    }

    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        final List<GameEvent> events = new ArrayList<>();
        
        if (player.getDebt() <= 0) {
            events.add(GameEventFactory.sendMessageEvent(
                    target.getName() + ": You don't owe us anything. Get back to work!", player.getId()));
            return events;
        }

        final int amountToPay = Math.min(player.getCredits(), player.getDebt());
        
        if (amountToPay > 0) {
            log.info("player {} paid {} credits", player.getName(), amountToPay);
            economyService.applyPayment(player, amountToPay);
            events.add(GameEventFactory.sendMessageEvent(
                    target.getName() + ": Payment received. Your remaining debt is " + player.getDebt() + " credits.", player.getId()));
            events.add(GameEventFactory.sendStatsEvent(player));
            
            if (player.getDebt() <= 0) {
                log.info("player {} paid all debt", player.getName());
                events.add(GameEventFactory.sendMessageEvent(
                        target.getName() + ": UNBELIEVABLE! You've paid it all! You are a free person now.", player.getId()));
            }
        } else {
            events.add(GameEventFactory.sendMessageEvent(
                    target.getName() + ": You don't have enough credits to make a payment! Current debt: " + player.getDebt(), player.getId()));
        }

        return events;
    }

    @Override
    public InteractionType getType() {
        return InteractionType.DEBT_PAYMENT;
    }
}
