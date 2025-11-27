package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents the J-shaped Tetris brick.
 * <p>
 * Stores all rotation states of the J-brick as 4x4 integer matrices,
 * where non-zero values indicate filled cells.
 * </p>
 */

final class JBrick implements Brick {
    /** Rotation states of the J-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();
    /**
     * Constructs a J-brick with its four rotation states.
     */

    public JBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
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
