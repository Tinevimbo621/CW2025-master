package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents the "I" Tetris brick .
 * <p>
 * This brick has two rotation states:
 * <ul>
 *     <li>Horizontal: 1×4 bar centered in a 4×4 matrix</li>
 *     <li>Vertical:   4×1 bar centered in a 4×4 matrix</li>
 * </ul>
 *
 * The brick layout uses:
 * <ul>
 *     <li>0 — empty cell</li>
 *     <li>1 — filled brick cell</li>
 * </ul>
 *
 * The shape matrices are stored internally and returned as deep copies
 * through {@link #getShapeMatrix()} to preserve immutability.
 */
final class IBrick implements Brick {

    /** Rotation matrices for the I brick . */
    private final List<int[][]> brickMatrix = new ArrayList<>();
    /**
     * Constructs an I-shaped Tetris brick with predefined rotation states.
     */
    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }
    /**
     * Returns a deep copy of the brick's rotation matrices.
     * <p>
     * Returning a deep copy ensures the internal shape definitions
     * remain immutable and cannot be modified by the game engine.
     *
     * @return a list of 4×4 integer matrices representing each rotation state.
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
