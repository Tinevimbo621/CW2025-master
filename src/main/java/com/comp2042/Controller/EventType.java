package com.comp2042.Controller;
/**
 * Represents the types of game events triggered during play.
 * <p>
 * DOWN      – move the active brick downward.<br>
 * LEFT      – move the active brick left.<br>
 * RIGHT     – move the active brick right.<br>
 * ROTATE    – rotate the active brick.<br>
 * HOLD      – hold the current brick.<br>
 * HARD_DROP – instantly drop the active brick to the bottom.
 * </p>
 */

public enum EventType {
    DOWN, LEFT, RIGHT, ROTATE, HOLD, HARD_DROP
}
