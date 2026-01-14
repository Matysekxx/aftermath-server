package cz.matysekxx.aftermathserver.core.logic.triggers;

import java.util.Optional;

public class TriggerRegistry {
    public Optional<TriggerHandler> getHandler(String type) {
        return Optional.empty();
    }
}
