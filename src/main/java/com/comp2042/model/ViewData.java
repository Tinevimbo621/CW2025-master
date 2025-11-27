package com.comp2042.model;
/**
 * Represents the data required to render the current game view.
 * <p>
 * Encapsulates the active brick, its position, the upcoming bricks,
 * and the held brick for display purposes.
 * </p>
 */

public final class ViewData {
    /** Matrix representing the currently active brick. */
    private final int[][] brickData;
    /** The horizontal position of the active brick. */
    private final int xPosition;
    /** The vertical position of the active brick. */
    private final int yPosition;
    /** Matrices representing the upcoming bricks (for preview). */
    private final int[][][] nextBricksData;
    /** Matrix representing the held brick (if any). */

    private final int [][] heldBrickData;
    /**
     * Constructs a new {@code ViewData} object.
     *
     * @param brickData      the matrix of the active brick
     * @param xPosition      the horizontal position of the active brick
     * @param yPosition      the vertical position of the active brick
     * @param nextBricksData the matrices of the upcoming bricks
     * @param heldBrickData  the matrix of the held brick
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][][] nextBricksData, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBricksData;
        this.heldBrickData = heldBrickData;
    }
    /**
     * Returns a copy of the active brick matrix.
     *
     * @return deep copy of the active brick matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }
    /**
     * Returns the horizontal position of the active brick.
     *
     * @return the x position
     */
    public int getxPosition() {
        return xPosition;
    }
    /**
     * Returns the vertical position of the active brick.
     *
     * @return the y position
     */
    public int getyPosition() {
        return yPosition;
    }
    /**
     * Returns a copy of the upcoming bricks matrices.
     *
     * @return deep copy of the next bricks data
     */
    public int[][][] getNextBricksData() {
        int[][][] copy = new int[nextBricksData.length][][];
        for (int i = 0; i < nextBricksData.length; i++) {
            copy[i] = MatrixOperations.copy(nextBricksData[i]);
        }
        return copy;
    }
    /**
     * Returns the held brick matrix.
     *
     * @return the held brick matrix, or {@code null} if none
     */
    public int[][] getHeldBrickData() { return heldBrickData; }
}
