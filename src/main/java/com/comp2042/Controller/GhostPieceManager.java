package com.comp2042.Controller;

import com.comp2042.model.Board;
import com.comp2042.model.ViewData;

/**
 * Manages the calculation and positioning logic for the ghost piece.
 * <p>
 * The ghost piece is a visual aid that shows where the active brick
 * would land if dropped instantly. This class encapsulates the logic
 * for determining its position, improving file organization and reducing
 * coupling in {@code GameController}.
 */

public class GhostPieceManager {
    private final Board board;

    /**
     * Constructs a GhostPieceManager with the specified board.
     *
     * @param board The game board
     */
    public GhostPieceManager(Board board) {
        this.board = board;
    }

    /**
     * Calculates the maximum drop distance for a ghost piece.
     *
     * @param brick The brick data
     * @return Maximum drop distance
     */
    public int getGhostDropDistance(ViewData brick) {
        int[][] grid = board.getBoardMatrix();
        int[][] shape = brick.getBrickData();
        int startX = brick.getxPosition();
        int startY = brick.getyPosition();

        return findMaximumDropDistance(shape, startX, startY, grid);
    }

    /**
     * Iteratively checks how far the brick can drop before colliding.
     *
     * @param shape the brick's shape matrix
     * @param x     the starting x-coordinate
     * @param y     the starting y-coordinate
     * @param grid  the game board matrix
     * @return the maximum drop distance
     */

    private int findMaximumDropDistance(int[][] shape, int x, int y, int[][] grid) {
        int maxDrop = 0;
        while (canPlaceShape(shape, x, y + maxDrop + 1, grid)) {
            maxDrop++;
        }
        return maxDrop;
    }
    /**
     * Checks if a brick shape can be placed at the given coordinates
     * without overlapping existing blocks or going out of bounds.
     *
     * @param shape the brick's shape matrix
     * @param x     the x-coordinate
     * @param y     the y-coordinate
     * @param grid  the game board matrix
     * @return {@code true} if placement is valid, {@code false} otherwise
     */

    private boolean canPlaceShape(int[][] shape, int x, int y, int[][] grid) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    if (!isValidShapePosition(row, col, x, y, grid)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Validates whether a single block of a brick can be placed
     * at the given grid coordinates.
     *
     * @param row  the row index within the shape
     * @param col  the column index within the shape
     * @param x    the x-coordinate on the board
     * @param y    the y-coordinate on the board
     * @param grid the game board matrix
     * @return {@code true} if the position is valid, {@code false} otherwise
     */

    private boolean isValidShapePosition(int row, int col, int x, int y, int[][] grid) {
        int gridY = y + row;
        int gridX = x + col;

        if (isOutOfBounds(gridY, gridX, grid)) {
            return false;
        }

        return grid[gridY][gridX] == 0;
    }
    /**
     * Checks whether the given coordinates are outside the board boundaries.
     *
     * @param y    the y-coordinate on the board
     * @param x    the x-coordinate on the board
     * @param grid the game board matrix
     * @return {@code true} if out of bounds, {@code false} otherwise
     */

    private boolean isOutOfBounds(int y, int x, int[][] grid) {
        return y >= grid.length || x < 0 || x >= grid[0].length;
    }
}