package com.comp2042.Controller;

import com.comp2042.model.Board;
import com.comp2042.model.ViewData;

/**
 * Manages the calculation and positioning logic for the ghost piece.
 * Separated to improve file organization and reduce coupling in GameController.
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


    private int findMaximumDropDistance(int[][] shape, int x, int y, int[][] grid) {
        int maxDrop = 0;
        while (canPlaceShape(shape, x, y + maxDrop + 1, grid)) {
            maxDrop++;
        }
        return maxDrop;
    }

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

    private boolean isValidShapePosition(int row, int col, int x, int y, int[][] grid) {
        int gridY = y + row;
        int gridX = x + col;

        if (isOutOfBounds(gridY, gridX, grid)) {
            return false;
        }

        return grid[gridY][gridX] == 0;
    }

    private boolean isOutOfBounds(int y, int x, int[][] grid) {
        return y >= grid.length || x < 0 || x >= grid[0].length;
    }
}