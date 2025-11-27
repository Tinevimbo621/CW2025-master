package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents the S-shaped Tetris brick.
 * <p>
 * Stores all rotation states of the S-brick as 4x4 integer matrices,
 * where non-zero values indicate filled cells.
 * </p>
 */
final class SBrick implements Brick {
    /** Rotation states of the S-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();
    /**
     * Constructs an S-brick with its two rotation states.
     */
    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
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
