package com.comp2042.model;
/**
 * Encapsulates information about the next Tetris shape to be displayed.
 * <p>
 * Stores the shape matrix and its intended horizontal position on the board.
 * </p>
 */

public final class  NextShapeInfo {
    /** The 2D matrix representing the shape. */
    private final int[][] shape;
    /** The horizontal position where the shape will appear. */

    private final int position;
    /**
     * Constructs a new {@code NextShapeInfo} with the given shape and position.
     *
     * @param shape    the 2D matrix representing the shape
     * @param position the horizontal position of the shape
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }
    /**
     * Returns a copy of the shape matrix.
     *
     * @return a deep copy of the shape matrix
     */

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }
    /**
     * Returns the horizontal position of the shape.
     *
     * @return the position value
     */

    public int getPosition() {
        return position;
    }
}
