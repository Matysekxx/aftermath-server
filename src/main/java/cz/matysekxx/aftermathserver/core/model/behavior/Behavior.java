package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.entity.Npc;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

/// Interface defining AI behavior for NPCs.
public interface Behavior {
    /// Executes the behavior logic for a specific NPC.
    void performAction(Npc npc, GameMapData gameMapData);
}
