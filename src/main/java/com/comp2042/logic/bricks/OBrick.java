package com.comp2042.logic.bricks;

import com.comp2042.model.MatrixOperations;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents the O-shaped Tetris brick (square).
 * <p>
 * The O-brick has only one rotation state, stored as a 4x4 integer matrix
 * where non-zero values indicate filled cells.
 * </p>
 */

final class OBrick implements Brick {
    /** Rotation states of the O-brick (only one state). */

    private final List<int[][]> brickMatrix = new ArrayList<>();
    /**
     * Constructs an O-brick with its single rotation state.
     */

    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }
    /**
     * Returns a deep copy of the brick's rotation states.
     *
     * @return list containing the single 4x4 matrix for the O-brick
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

}
