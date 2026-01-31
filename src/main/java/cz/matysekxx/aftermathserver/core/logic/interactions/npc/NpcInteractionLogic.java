package cz.matysekxx.aftermathserver.core.logic.interactions.npc;

import cz.matysekxx.aftermathserver.core.model.entity.InteractionType;
import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import cz.matysekxx.aftermathserver.event.GameEvent;

import java.util.Collection;

public interface NpcInteractionLogic {

    Collection<GameEvent> interact(Npc target, Player player);

    InteractionType getType();
}
