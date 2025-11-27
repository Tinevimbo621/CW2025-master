package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents the L-shaped Tetris brick.
 * <p>
 * Stores all rotation states of the L-brick as 4x4 integer matrices,
 * where non-zero values indicate filled cells.
 * </p>
 */

final class LBrick implements Brick {
    /** Rotation states of the L-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();
    /**
     * Constructs an L-brick with its four rotation states.
     */
    public LBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 0, 0}
        });
    }
    /**
     * Returns a deep copy of the brick's rotation states.
     *
     * @return list of 4x4 matrices representing each rotation
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
