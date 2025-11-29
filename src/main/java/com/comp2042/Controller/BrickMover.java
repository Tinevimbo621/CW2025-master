package com.comp2042.Controller;

import com.comp2042.model.Board;


/**
 * Service class responsible for encapsulating all active brick manipulation logic
 * (movement, rotation, and hold operations) by delegating to the Board implementation.
 * This separates the movement mechanics from the high-level game flow in GameController.
 */
public class BrickMover {

    private final Board board;

    /**
     * Constructs a BrickMover tied to a specific game board.
     * @param board The game board instance.
     */
    public BrickMover(Board board) {
        this.board = board;
    }

    /**
     * Delegates the left movement to the board.
     * @return true if the movement was successful.
     */
    public boolean moveLeft() {
        return board.moveBrickLeft();
    }

    /**
     * Delegates the right movement to the board.
     * @return true if the movement was successful.
     */
    public boolean moveRight() {
        return board.moveBrickRight();
    }

    /**
     * Delegates the rotation to the board.
     * @return true if the rotation was successful.
     */
    public boolean rotate() {
        return board.rotateLeftBrick();
    }

    /**
     * Delegates the downward movement to the board.
     * @return true if the movement was successful.
     */
    public boolean drop() {
        return board.moveBrickDown();
    }

    /**
     * Delegates the hold operation to the board.
     */
    public void hold() {
        board.holdBrick();
    }

    public boolean canMoveDown() {
        return board.moveBrickDown();
    }
}