package cz.matysekxx.aftermathserver.core.logic.triggers;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class TriggerRegistry {
    private final Map<String, TriggerHandler> handlers;

    public TriggerRegistry(Map<String, TriggerHandler> handlers) {
        this.handlers = handlers;
    }

    public Optional<TriggerHandler> getHandler(String type) {
        return handlers.containsKey(type) ? Optional.of(handlers.get(type)) : Optional.empty();
    }
}
