package com.comp2042.model;

/**
 * Represents the core logic and state of a Tetris-like game board.
 * Implementations of this interface handle:
 * <ul>
 *     <li>Brick movement (down, left, right)</li>
 *     <li>Brick rotation</li>
 *     <li>Spawning new bricks</li>
 *     <li>Collision detection</li>
 *     <li>Board matrix management</li>
 *     <li>Score + row clearing logic</li>
 *     <li>Hold brick functionality</li>
 * </ul>
 */
public interface   Board {
    /**
     * Attempts to move the current brick one row downward.
     *
     * @return {@code true} if the brick successfully moved down,
     *         {@code false} if it could not move and should be merged.
     */
    boolean moveBrickDown();
    /**
     * Attempts to move the current brick one column to the left.
     *
     * @return {@code true} if the movement was successful;
     *         {@code false} otherwise.
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the current brick one column to the right.
     *
     * @return {@code true} if the movement was successful; {@code false} otherwise.
     */
    boolean moveBrickRight();
    /**
     * Attempts to rotate the current brick to the left (counter-clockwise).
     *
     * @return {@code true} if the rotation was valid and applied;
     *         {@code false} otherwise.
     */
    boolean rotateLeftBrick();
    /**
     * Creates a new brick at the top of the board.
     *
     * @return {@code true} if the brick was created successfully;
     *         {@code false} if spawning the brick is impossible (game over).
     */
    boolean createNewBrick();
    /**
     * Returns the current state of the game board, excluding the active falling brick.
     * <p>
     * This matrix contains:
     * <ul>
     *     <li>0 for empty cells</li>
     *     <li>color/value codes for placed bricks</li>
     * </ul>
     *
     * @return a 2D matrix representing the board.
     */

    int[][] getBoardMatrix();

    /**
     * Returns the full view data combining the board matrix and active brick state.
     * <p>
     * This is used by the GUI layer to render the board and brick.
     *
     * @return a {@link ViewData} object containing current game state.
     */
    ViewData getViewData();

    /**
     * Merges the active falling brick permanently into the board matrix.
     * <p>
     * This should be called when the brick can no longer move down.
     */
    void mergeBrickToBackground();
    /**
     * Clears full rows from the board.
     *
     * @return a {@link ClearRow} object containing:
     *         <ul>
     *             <li>number of cleared rows</li>
     *             <li>score bonus</li>
     *         </ul>
     */
    ClearRow clearRows();
    /**
     * Returns the player's current score.
     *
     * @return a {@link Score} object representing score state.
     */
    Score getScore();
    /**
     * Resets the board and starts a new game.
     * This should:
     * <ul>
     *     <li>Clear the board matrix</li>
     *     <li>Reset score</li>
     *     <li>Spawn a new brick</li>
     *     <li>Clear hold state</li>
     * </ul>
     */
    void newGame();

    /**
     * Executes the "hold brick" logic.
     * <p>
     * This should:
     * <ul>
     *     <li>Swap the current brick with the held brick</li>
     *     <li>Prevent double-holding until the next brick spawns</li>
     * </ul>
     */
    void holdBrick();
}
