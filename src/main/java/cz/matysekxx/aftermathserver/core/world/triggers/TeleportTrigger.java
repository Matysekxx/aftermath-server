package cz.matysekxx.aftermathserver.core.world.triggers;

import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/// Trigger definition for teleportation tiles.
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TeleportTrigger extends TileTrigger {
    private int targetX;
    private int targetY;
    private int targetLayer;

    public TeleportTrigger(int targetX, int targetY, int targetLayer) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetLayer = targetLayer;
    }

    @Override
    public void onEnter(Player player, TriggerContext context) {
        player.setLayerIndex(targetLayer);
        player.setX(targetX);
        player.setY(targetY);
    }
}
