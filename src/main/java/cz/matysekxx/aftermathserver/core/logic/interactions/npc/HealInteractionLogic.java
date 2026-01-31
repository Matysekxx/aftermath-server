package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class HealInteractionLogic implements NpcInteractionLogic {
    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        final int cost = 20;
        if (player.getHp() < player.getMaxHp() || player.getRads() > 0){
            if (player.getCredits() < cost) {
                return List.of(GameEventFactory.sendErrorEvent("You don't have enough credits! Price: " + cost, player.getId()));
            }
            player.setRads(0);
            player.setHp(player.getMaxHp());
            player.removeCredits(cost);
            return List.of(
                    GameEventFactory.sendMessageEvent("You are now fully healed", player.getId()),
                    GameEventFactory.sendStatsEvent(player)
            );
        } else {
            return List.of(GameEventFactory.sendMessageEvent("You are fully healed you don't need to heal", player.getId()));
        }
    }

    @Override
    public InteractionType getType() {
        return InteractionType.HEAL;
    }
}
