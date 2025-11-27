package com.comp2042.model;

import com.comp2042.LeaderBoard.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
/**
 * Utility class providing matrix operations used by the Tetris game logic.
 * <p>
 * All methods are static, and the class cannot be instantiated.
 * Responsibilities include:
 * <ul>
 *     <li>Collision detection (brick vs. board)</li>
 *     <li>Deep-copy utilities</li>
 *     <li>Merging a falling brick into the board</li>
 *     <li>Detecting and removing full rows</li>
 * </ul>
 */
public class MatrixOperations {

    /** Private constructor to prevent instantiation. */
    private MatrixOperations(){

    }
    /**
     * Checks whether a brick placed at board position (x, y) would collide
     * with either the board boundaries or occupied cells.
     *
     * @param matrix the game board matrix
     * @param brick  the brick shape matrix (4×4 or similar)
     * @param x      board X coordinate of the brick
     * @param y      board Y coordinate of the brick
     * @return {@code true} if collision occurs, {@code false} otherwise
     */
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {

                if (brick[i][j] == 0)
                    continue;
                int targetX = x + j;
                int targetY = y + i;

                if (isOutOfBounds(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Checks whether the given cell coordinates fall outside the board area.
     *
     * @param matrix  the game board
     * @param targetX target X position
     * @param targetY target Y position
     * @return {@code true} if outside bounds, {@code false} otherwise
     */
    private static boolean isOutOfBounds(int[][] matrix, int targetX, int targetY) {
        return !(targetX >= 0 &&
                targetY < matrix.length &&
                targetX < matrix[targetY].length);
    }

    /**
     * Creates a deep copy of a 2D matrix.
     *
     * @param original the source matrix
     * @return a new deep-copied matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = new int[original[i].length];
            System.arraycopy(original[i], 0, result[i], 0, original[i].length);
        }
        return result;
    }
    /**
     * Merges a brick into the board at the specified location.
     * Produces a new matrix without modifying the original board.
     *
     * @param filledFields the board matrix
     * @param brick        the brick matrix
     * @param x            brick top-left X position on board
     * @param y            brick top-left Y position on board
     * @return a new merged matrix
     */
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);

        for (int row = 0; row < brick.length; row++) {
            for (int col = 0; col < brick[row].length; col++) {

                if (brick[row][col] == 0)
                    continue;

                int targetX = x + col;
                int targetY = y + row;

                copy[targetY][targetX] = brick[row][col];
            }
        }
        return copy;
    }


    /**
     * Checks the board for full rows, removes them, and shifts upper rows down.
     *
     * @param matrix the board matrix
     * @return a {@link ClearRow} object containing:
     *         <ul>
     *             <li>Number of cleared rows</li>
     *             <li>The new board matrix</li>
     *             <li>The score bonus granted</li>
     *         </ul>
     */
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();

        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Deep copies a list of int[][] matrices.
     *
     * @param list the list to copy
     * @return a new list containing deep-copied matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
