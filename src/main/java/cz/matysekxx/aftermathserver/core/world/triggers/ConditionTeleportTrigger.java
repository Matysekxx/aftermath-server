package cz.matysekxx.aftermathserver.core.world.triggers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.matysekxx.aftermathserver.core.model.Player;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.function.Predicate;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionTeleportTrigger extends TeleportTrigger {
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
            final Boolean result = exp.getValue(player, Boolean.class);
            return result != null && result;
        };
    }
}
