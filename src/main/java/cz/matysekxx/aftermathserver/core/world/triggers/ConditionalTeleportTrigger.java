package cz.matysekxx.aftermathserver.core.world.triggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.function.Predicate;

/// Trigger definition for conditional teleportation.
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ConditionalTeleportTrigger extends TeleportTrigger {
    private String condition;
    @JsonIgnore
    private Predicate<Player> predicate;

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
            log.info("player {} has condition {}", player, condition);
            final Boolean result = exp.getValue(player, Boolean.class);
            log.info("player {} has result {}", player, result);
            return result != null && result;
        };
    }
}
