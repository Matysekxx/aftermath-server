package cz.matysekxx.aftermathserver.core.model.entity;

/**
 * Represents the current state of an entity.
 *
 * @author Matysekxx
 */
public enum State {
    /** Entity is alive and active. */
    ALIVE,
    /** Entity is dead. */
    DEAD,
    /** Entity is suffering from poison/radiation effects. */
    POISONED
}