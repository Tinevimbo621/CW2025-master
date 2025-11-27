package com.comp2042.model;

import com.comp2042.LeaderBoard.ClearRow;
/**
 * Immutable data container representing the result of a downward brick move.
 *
 * <p>This object wraps two key components:</p>
 * <ul>
 *     <li>{@link ClearRow} — Information about cleared rows and score bonuses</li>
 *     <li>{@link ViewData} — The updated board state for the renderer</li>
 * </ul>
 *
 * <p>It is returned after a brick is moved downward, indicating both visual updates
 * and game logic changes (such as line clears).</p>
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;
    /**
     * Creates a new immutable DownData container.
     *
     * @param clearRow information about removed rows and scoring; may be {@code null} if no rows were cleared
     * @param viewData updated board view data; must not be {@code null}
     */
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /**
     * Returns the row-clearing result of the move.
     *
     * @return a {@link ClearRow} instance, or {@code null} if no rows were cleared
     */
    public ClearRow getClearRow() {
        return clearRow;
    }
    /**
     * Returns the updated board view after the move.
     *
     * @return the current {@link ViewData} representation of the board
     */
    public ViewData getViewData() {
        return viewData;
    }
}
