package com.comp2042.controller;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.model.Board;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GhostPieceManagerTest {

    /**
     * Helper stub for Board
     */
    static class TestBoard implements Board {
        private final int[][] grid;

        public TestBoard(int[][] grid) {
            this.grid = grid;
        }

        /**
         * Attempts to move the current brick one row downward.
         *
         * @return {@code true} if the brick successfully moved down,
         * {@code false} if it could not move and should be merged.
         */
        @Override
        public boolean moveBrickDown() {
            return false;
        }

        /**
         * Attempts to move the current brick one column to the left.
         *
         * @return {@code true} if the movement was successful;
         * {@code false} otherwise.
         */
        @Override
        public boolean moveBrickLeft() {
            return false;
        }

        /**
         * Attempts to move the current brick one column to the right.
         *
         * @return {@code true} if the movement was successful; {@code false} otherwise.
         */
        @Override
        public boolean moveBrickRight() {
            return false;
        }

        /**
         * Attempts to rotate the current brick to the left (counter-clockwise).
         *
         * @return {@code true} if the rotation was valid and applied;
         * {@code false} otherwise.
         */
        @Override
        public boolean rotateLeftBrick() {
            return false;
        }

        /**
         * Creates a new brick at the top of the board.
         *
         * @return {@code true} if the brick was created successfully;
         * {@code false} if spawning the brick is impossible (game over).
         */
        @Override
        public boolean createNewBrick() {
            return false;
        }

        @Override
        public int[][] getBoardMatrix() {
            return grid;
        }

        /**
         * Returns the full view data combining the board matrix and active brick state.
         * <p>
         * This is used by the GUI layer to render the board and brick.
         *
         * @return a {@link ViewData} object containing current game state.
         */
        @Override
        public ViewData getViewData() {
            return null;
        }

        /**
         * Merges the active falling brick permanently into the board matrix.
         * <p>
         * This should be called when the brick can no longer move down.
         */
        @Override
        public void mergeBrickToBackground() {

        }

        /**
         * Clears full rows from the board.
         *
         * @return a {@link ClearRow} object containing:
         * <ul>
         *     <li>number of cleared rows</li>
         *     <li>score bonus</li>
         * </ul>
         */
        @Override
        public ClearRow clearRows() {
            return null;
        }

        /**
         * Returns the player's current score.
         *
         * @return a {@link Score} object representing score state.
         */
        @Override
        public Score getScore() {
            return null;
        }

        /**
         * Resets the board and starts a new game.
         * This should:
         * <ul>
         *     <li>Clear the board matrix</li>
         *     <li>Reset score</li>
         *     <li>Spawn a new brick</li>
         *     <li>Clear hold state</li>
         * </ul>
         */
        @Override
        public void newGame() {

        }

        /**
         * Executes the "hold brick" logic.
         * <p>
         * This should:
         * <ul>
         *     <li>Swap the current brick with the held brick</li>
         *     <li>Prevent double-holding until the next brick spawns</li>
         * </ul>
         */
        @Override
        public void holdBrick() {

        }
    }

    /**
     * Helper stub for ViewData
     */
    static class TestViewData extends ViewData {
        private final int[][] brickData;
        private final int x;
        private final int y;

        public TestViewData(int[][] brickData, int x, int y) {
            super(brickData, x, y, null, null); // <- Call required ViewData constructor
            this.brickData = brickData;
            this.x = x;
            this.y = y;
        }

        @Override
        public int[][] getBrickData() {
            return brickData;
        }

        @Override
        public int getxPosition() {
            return x;
        }

        @Override
        public int getyPosition() {
            return y;
        }
    }

    // -----------------------------------------------------
    // TEST 1: EMPTY BOARD → BRICK SHOULD FALL TO BOTTOM
    // -----------------------------------------------------
    @Test
    void testGhostDropOnEmptyBoard() {
        int[][] grid = new int[20][10]; // 20 rows, 10 cols

        int[][] shape = {
                {1, 1},
                {1, 1}
        }; // O-block

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 4, 0);

        int drop = gm.getGhostDropDistance(brick);

        assertEquals(18, drop, "O-block should fall to row 20 on empty board");
    }

    // -----------------------------------------------------
    // TEST 2: BRICK DIRECTLY ABOVE AN OBSTACLE
    // -----------------------------------------------------
    @Test
    void testGhostDropStopsAtExistingBlock() {
        int[][] grid = new int[20][10];
        grid[10][4] = 9; // obstacle at row 10 col 4

        int[][] shape = {
                {1, 1},
                {1, 1}
        };

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 4, 0);

        int drop = gm.getGhostDropDistance(brick);

        // Position 10 is blocked, so O-block stops at row 8
        assertEquals(8, drop, "O-block should stop at row 8 because row 10 is occupied");
    }

    // -----------------------------------------------------
    // TEST 3: BRICK ALREADY AT BOTTOM → DROP == 0
    // -----------------------------------------------------
    @Test
    void testNoDropWhenAtBottom() {
        int[][] grid = new int[20][10];

        int[][] shape = {
                {1},
                {1},
                {1},
                {1}
        }; // I-block vertical

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 3, 16); // at row 16, height=4, bottom=19

        int drop = gm.getGhostDropDistance(brick);

        assertEquals(0, drop, "Vertical I-block at the bottom should not drop");
    }

    // -----------------------------------------------------
    // TEST 4: BRICK PARTIALLY OUT OF BOUNDS (TOP)
    // Ghost logic must handle negative Y gracefully.
    // -----------------------------------------------------
    @Test
    void testBrickStartingAboveBoard() {
        int[][] grid = new int[30][10];

        int[][] shape = {
                {0, 3, 3, 3},
                {0, 3, 0, 0},
        };

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 3, -1);

        int drop = gm.getGhostDropDistance(brick);

        assertEquals(29, drop, "Piece above board should still fall to the bottom");
    }

    // -----------------------------------------------------
    // TEST 5: SHAPE NEXT TO WALL
    // Ensure X boundaries are respected.
    // -----------------------------------------------------
    @Test
    void testGhostDropNearLeftWall() {
        int[][] grid = new int[20][10];

        int[][] shape = {

                {0, 1, 1},
                {1, 1, 0},

        };

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 0, 5); // flush to left wall

        int drop = gm.getGhostDropDistance(brick);

        assertEquals(13, drop, "Piece near left wall should still fall unrestricted");
    }

    // -----------------------------------------------------
    // TEST 6: COMPLEX SHAPE COLLISION CHECK
    // -----------------------------------------------------
    @Test
    void testGhostDropWithComplexCollision() {
        int[][] grid = new int[20][10];

        // Place a staircase-like structure
        grid[15][3] = 9;
        grid[16][4] = 9;
        grid[17][5] = 9;

        int[][] shape = {
                {0, 1, 0},
                {1, 1, 1}
        }; // T-block

        GhostPieceManager gm = new GhostPieceManager(new TestBoard(grid));
        ViewData brick = new TestViewData(shape, 3, 5);

        int drop = gm.getGhostDropDistance(brick);

        assertEquals(8, drop, "T-block should fall until collision with stair structure");
    }
}
