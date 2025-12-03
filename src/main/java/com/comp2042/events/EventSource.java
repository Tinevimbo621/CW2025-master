package com.comp2042.events;
/**
 * Represents the origin of a game event.
 * USER   – events triggered by player actions (for example, key presses).<br>
 * THREAD – events triggered by background processes or game loops.
 */
public enum EventSource {
    /** Events triggered by player actions (forexample, key presses). */
    USER,
    /** Events triggered by background processes or game loops. */

    THREAD
}
