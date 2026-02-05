package cz.matysekxx.aftermathserver.core.world.triggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.model.entity.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.function.Predicate;

/// Represents a single station in the metro network.
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
@NoArgsConstructor
public class ConditionalTeleportTrigger extends TeleportTrigger {
    private String condition;
    @JsonIgnore
    private Predicate<Player> predicate;

    public ConditionalTeleportTrigger(int targetX, int targetY, int targetLayer, String condition) {
        super(targetX, targetY, targetLayer);
        setCondition(condition);
    }

    public void setCondition(String condition) {
        this.condition = condition;
        this.predicate = buildPredicate(condition);
    }

    private Predicate<Player> buildPredicate(String condition) {
        if (condition == null || condition.isEmpty()) {
            return player -> true;
        }
        final SpelExpressionParser parser = new SpelExpressionParser();
        final Expression exp = parser.parseExpression(condition);
        return player -> {
            final Boolean result = exp.getValue(player, Boolean.class);
            return result != null && result;
        };
    }

    @Override
    public void onEnter(Player player, TriggerContext context) {
        if (predicate == null || predicate.test(player)) {
            super.onEnter(player, context);
        }
    }
}