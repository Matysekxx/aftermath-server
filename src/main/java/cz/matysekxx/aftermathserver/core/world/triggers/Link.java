package cz.matysekxx.aftermathserver.core.world.triggers;

/**
 * Represents a link between two maps, used for generating teleport triggers.
 *
 * @param from The source map marker ID.
 * @param to   The destination map marker ID.
 * @author Matysekxx
 */
public record Link(String from, String to) {
}
