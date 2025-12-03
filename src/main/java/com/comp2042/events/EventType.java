package com.comp2042.events;
/**
 * Represents the types of game events triggered during play.
 * <p>
 * Each event corresponds to an action performed by the player:
 * </p>
 * <ul>
 *   <li>{@link #DOWN}      – move the active brick downward.</li>
 *   <li>{@link #LEFT}      – move the active brick left.</li>
 *   <li>{@link #RIGHT}     – move the active brick right.</li>
 *   <li>{@link #ROTATE}    – rotate the active brick.</li>
 *   <li>{@link #HOLD}      – hold the current brick.</li>
 *   <li>{@link #HARD_DROP} – instantly drop the active brick to the bottom.</li>
 * </ul>
 */

public enum EventType {
    /** Move the active brick downward. */
    DOWN,
    /** Move the active brick to the left. */
    LEFT,
    /** Move the active brick to the right. */
    RIGHT,
    /** Rotate the active brick clockwise. */
    ROTATE,
    /** Hold the current brick for later use. */
    HOLD,
    /** Instantly drop the active brick to the bottom. */
    HARD_DROP
}
