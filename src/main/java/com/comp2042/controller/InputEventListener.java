package com.comp2042.controller;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
/**
 * Listener interface for handling player input events in the game.
 * <p>
 * Defines callbacks for movement, rotation, hard drop, hold, and game reset actions.
 * </p>
 */
public interface InputEventListener {
    /**
     * Handles a "move down" event.
     *
     * @param event the move event details
     * @return data about the board state after the move
     */
    DownData onDownEvent(MoveEvent event);
    /**
     * Handles a "move left" event.
     *
     * @param event the move event details
     * @return updated view data
     */
    ViewData onLeftEvent(MoveEvent event);
    /**
     * Handles a "move right" event.
     *
     * @param event the move event details
     * @return updated view data
     */
    ViewData onRightEvent(MoveEvent event);
    /**
     * Handles a "rotate" event.
     *
     * @param event the move event details
     * @return updated view data
     */
    ViewData onRotateEvent(MoveEvent event);
    /**
     * Handles a "hard drop" event (instantly drop brick to bottom).
     *
     * @param event the move event details
     * @return updated view data
     */
    ViewData onHardDropEvent(MoveEvent event);
    /**
     * Starts a new game.
     */
    void createNewGame();
    /**
     * Handles a "hold" event (store or swap the current brick).
     *
     * @param moveEvent the move event details
     * @return updated view data
     */
    ViewData onHoldEvent(MoveEvent moveEvent);
}
