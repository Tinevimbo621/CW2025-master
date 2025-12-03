package com.comp2042.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBoardTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        // Create a board with width 10 and height 20
        board = new SimpleBoard(10, 20);
    }

    @AfterEach
    void tearDown() {
        board = null;
    }

    @Test
    void moveBrickDown() {
        board.createNewBrick();
        assertTrue(board.moveBrickDown(), "Brick should move down on empty board");
    }

    @Test
    void moveBrickLeft() {
        board.createNewBrick();
        assertTrue(board.moveBrickLeft(), "Brick should move left on empty board");
    }

    @Test
    void moveBrickRight() {
        board.createNewBrick();
        assertTrue(board.moveBrickRight(), "Brick should move right on empty board");
    }

    @Test
    void rotateLeftBrick() {
        board.createNewBrick();
        assertTrue(board.rotateLeftBrick(), "Brick should rotate successfully");
    }

    @Test
    void createNewBrick() {
        boolean conflict = board.createNewBrick();
        assertFalse(conflict, "New brick should spawn without conflict on empty board");
    }

    @Test
    void getBoardMatrix() {
        int[][] matrix = board.getBoardMatrix();
        assertEquals(20, matrix.length, "Board should have 20 rows");
        assertEquals(10, matrix[0].length, "Board should have 10 columns");
    }


    @Test
    void mergeBrickToBackground() {
        board.createNewBrick();
        board.mergeBrickToBackground();
        int[][] matrix = board.getBoardMatrix();
        boolean hasBlocks = false;
        for (int[] row : matrix) {
            for (int cell : row) {
                if (cell != 0) {
                    hasBlocks = true;
                    break;
                }
            }
        }
        assertTrue(hasBlocks, "Board should contain merged brick cells");
    }

    @Test
    void clearRows() {
        int[][] matrix = board.getBoardMatrix();
        // Fill the bottom row manually
        for (int x = 0; x < matrix[0].length; x++) {
            matrix[matrix.length - 1][x] = 1;
        }
        ClearRow clearRow = board.clearRows();
        assertTrue(clearRow.getLinesRemoved() > 0, "At least one row should be cleared");
    }

    @Test
    void getScore() {
        assertEquals(0, board.getScore().getScore(), "Initial score should be 0");
    }

    @Test
    void newGame() {
        board.createNewBrick();
        board.mergeBrickToBackground();
        board.getScore().add(100); // assuming Score has addPoints
        board.newGame();

        int[][] matrix = board.getBoardMatrix();
        for (int[] row : matrix) {
            for (int cell : row) {
                assertEquals(0, cell, "Board should be cleared after newGame()");
            }
        }
        assertEquals(0, board.getScore().getScore(), "Score should reset to 0");
    }

    @Test
    void holdBrick() {
        board.createNewBrick();
        board.holdBrick();
        assertNotNull(board.getHeldBrickData(), "Held brick should not be null after holding");
    }

    @Test
    void getHeldBrickData() {
        board.createNewBrick();
        board.holdBrick();
        int[][] held = board.getHeldBrickData();
        assertNotNull(held, "Held brick data should not be null");
    }
}