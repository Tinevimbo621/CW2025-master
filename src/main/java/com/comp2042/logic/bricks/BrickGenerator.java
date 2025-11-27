package com.comp2042.logic.bricks;

/**
 * Defines a generator for producing Tetris bricks.
 * <p>
 * Provides methods to obtain a new random brick and preview the next brick.
 * </p>
 */

public interface BrickGenerator {

    /**
     * Returns the next brick from the generator queue.
     * @deprecated Replaced with queue-based next-bricks system.
     */
    @Deprecated
    Brick getNextBrick();
    /**
     * Returns the next brick that the player will use.
     * <p>
     * This method is typically called whenever the current brick locks into
     * the board and a new one must spawn. The brick returned should be the
     * next item from the internal brick queue (such as a 7-bag generator).
     * </p>
     *
     * @return a newly generated {@link Brick} instance
     */
    Brick getBrick();

}
