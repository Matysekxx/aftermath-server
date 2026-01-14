package cz.matysekxx.aftermathserver.core.model.behavior;

import cz.matysekxx.aftermathserver.core.model.Npc;
import cz.matysekxx.aftermathserver.core.world.GameMapData;

public interface Behavior  {
    void performAction(Npc npc, GameMapData gameMapData);
}
