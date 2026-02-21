package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.DialogRegistry;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * Logic for handling healing interactions with NPCs.
 *
 * @author Matysekxx
 */
@Component
public class HealInteractionLogic implements NpcInteractionLogic {
    private final DialogRegistry dialogRegistry;

    public HealInteractionLogic(DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
    }

    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        final List<GameEvent> events = new ArrayList<>();

        String text = dialogRegistry.getRandomDialog(target.getDialogueId());
        if (text != null) {
            events.add(GameEventFactory.sendDialogEvent(target.getName(), text, player.getId()));
        }

        final int cost = 20;
        if (player.getHp() < player.getMaxHp() || player.getRads() > 0) {
            if (player.getCredits() < cost) {
                events.add(GameEventFactory.sendErrorEvent("You don't have enough credits! Price: " + cost, player.getId()));
                return events;
            }
            player.setRads(0);
            player.setHp(player.getMaxHp());
            player.removeCredits(cost);
            events.add(GameEventFactory.sendMessageEvent("You are now fully healed", player.getId()));
            events.add(GameEventFactory.sendStatsEvent(player));
        } else {
            events.add(GameEventFactory.sendMessageEvent("You are fully healed you don't need to heal", player.getId()));
        }
        return events;
    }

    @Override
    public InteractionType getType() {
        return InteractionType.HEAL;
    }
}
