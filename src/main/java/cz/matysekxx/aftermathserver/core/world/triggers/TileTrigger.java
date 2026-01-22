package cz.matysekxx.aftermathserver.core.world.triggers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/// Base class for all tile-based triggers.
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TeleportTrigger.class, name = "TELEPORT"),
        @JsonSubTypes.Type(value = ConditionTeleportTrigger.class, name = "CONDITION_TELEPORT"),
        @JsonSubTypes.Type(value = DamageTrigger.class, name = "DAMAGE"),
        @JsonSubTypes.Type(value = MetroTrigger.class, name = "METRO_TRAVEL"),
        @JsonSubTypes.Type(value = HealTrigger.class, name = "HEAL")
})
public abstract class TileTrigger {
    protected String type;
}