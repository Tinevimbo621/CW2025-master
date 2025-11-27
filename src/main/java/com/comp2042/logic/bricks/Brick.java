package com.comp2042.logic.bricks;

import java.util.List;
/**
 * Represents a generic Tetris brick (tetromino).
 * <p>
 * Each brick has one or more rotation states. Every rotation state is
 * represented as a 4×4 integer matrix. The matrix uses:
 *
 * <ul>
 *     <li>0 — empty cell</li>
 *     <li>non-zero value — filled brick cell (specific value defines color/type)</li>
 * </ul>
 *
 * Implementations (e.g., IBrick, TBrick, OBrick) should define all their
 * rotation matrices and return **deep copies** to preserve immutability.
 */
public interface Brick {
    /**
     * Returns the list of rotation matrices for this brick.
     * <p>
     * The returned list must:
     * <ul>
     *     <li>Contain one matrix per rotation state</li>
     *     <li>Use 4×4 matrices for consistent positioning</li>
     *     <li>Be a deep copy — callers must not be able to modify internal data</li>
     * </ul>
     *
     * @return a list of 4×4 int matrices representing each rotation state
     */
    List<int[][]> getShapeMatrix();
}
