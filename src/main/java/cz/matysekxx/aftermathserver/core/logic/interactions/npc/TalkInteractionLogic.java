package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.DialogRegistry;
import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;
import cz.matysekxx.aftermathserver.event.GameEventFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Logic for handling simple talk interactions with NPCs.
 *
 * @author Matysekxx
 */
@Component
public class TalkInteractionLogic implements NpcInteractionLogic {
    private final DialogRegistry dialogRegistry;

    public TalkInteractionLogic(DialogRegistry dialogRegistry) {
        this.dialogRegistry = dialogRegistry;
    }

    @Override
    public Collection<GameEvent> interact(Npc target, Player player) {
        String text = dialogRegistry.getRandomDialog(target.getDialogueId());
        if (text == null) {
            text = "...";
        }
        return List.of(GameEventFactory.sendDialogEvent(target.getName(), text, player.getId()));
    }

    @Override
    public InteractionType getType() {
        return InteractionType.TALK;
    }
}
