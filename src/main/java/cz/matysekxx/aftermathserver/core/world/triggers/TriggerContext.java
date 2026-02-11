package cz.matysekxx.aftermathserver.core.world.triggers;

import cz.matysekxx.aftermathserver.core.logic.metro.MetroService;

/**
 * Context object passed to triggers during execution.
 * Provides access to necessary services.
 *
 * @param metroService The metro service instance.
 * @author Matysekxx
 */
public record TriggerContext(MetroService metroService) {
}