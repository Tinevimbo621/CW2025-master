package com.comp2042.controller;
/**
 * Represents a game action request such as move, rotate, or drop.
 * Contains the action type and the source that triggered it.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;
    /**
     * Constructs a new {@code MoveEvent}.
     *
     * @param eventType   the type of move or action requested
     * @param eventSource the source that triggered the event
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }
    /**
     * Returns the type of movement/action represented by this event.
     *
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }
    /**
     * Returns the origin of the event.
     *
     * @return the event source
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}
