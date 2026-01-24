package cz.matysekxx.aftermathserver.core.world.triggers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.Data;

/// Base class for all tile-based triggers.
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TeleportTrigger.class, name = "TELEPORT"),
        @JsonSubTypes.Type(value = ConditionalTeleportTrigger.class, name = "CONDITION_TELEPORT"),
        @JsonSubTypes.Type(value = DamageTrigger.class, name = "DAMAGE"),
        @JsonSubTypes.Type(value = MetroTrigger.class, name = "METRO_TRAVEL"),
        @JsonSubTypes.Type(value = HealTrigger.class, name = "HEAL"),
})
public abstract class TileTrigger {
    /// Executes the trigger logic when a player enters the tile.
    public abstract void onEnter(Player player, TriggerContext context);
}