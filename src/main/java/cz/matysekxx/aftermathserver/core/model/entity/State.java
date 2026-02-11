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
    /** Player is currently travelling via metro (not on map). */
    TRAVELLING,
    /** Entity is suffering from poison/radiation effects. */
    POISONED
}