package cz.matysekxx.aftermathserver.core.logic.triggers;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/// Registry for looking up TriggerHandlers by type.
@Component
public class TriggerRegistry {
    private final Map<String, TriggerHandler> handlers;

    public TriggerRegistry(Map<String, TriggerHandler> handlers) {
        this.handlers = handlers;
    }

    /// Retrieves a handler for the given trigger type.
    ///
    /// @param type The type string of the trigger (e.g., "TELEPORT", "DAMAGE").
    /// @return An Optional containing the handler if found.
    public Optional<TriggerHandler> getHandler(String type) {
        return handlers.containsKey(type) ? Optional.of(handlers.get(type)) : Optional.empty();
    }
}
