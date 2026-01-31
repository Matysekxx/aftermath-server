package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class TalkInteractionLogic implements NpcInteractionLogic {
    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        return List.of();
    }

    @Override
    public InteractionType getType() {
        return InteractionType.TALK;
    }
}
