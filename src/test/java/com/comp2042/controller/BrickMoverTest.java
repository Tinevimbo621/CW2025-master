package com.comp2042.controller;

import com.comp2042.model.Board;
import com.comp2042.model.ClearRow;
import com.comp2042.model.Score;
import com.comp2042.model.ViewData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickMoverTest {

    private BrickMover mover;
    private FakeBoard fakeBoard;

    @BeforeEach
    void setUp() {
        fakeBoard = new FakeBoard();
        mover = new BrickMover(fakeBoard);
    }

    @Test
    void moveLeft() {
        fakeBoard.moveLeftResult = true;
        assertTrue(mover.moveLeft());
        assertTrue(fakeBoard.moveLeftCalled);
    }

    @Test
    void moveRight() {
        fakeBoard.moveRightResult = false;
        assertFalse(mover.moveRight());
        assertTrue(fakeBoard.moveRightCalled);
    }

    @Test
    void rotate() {
        fakeBoard.rotateResult = true;
        assertTrue(mover.rotate());
        assertTrue(fakeBoard.rotateCalled);
    }

    @Test
    void drop() {
        fakeBoard.dropResult = true;
        assertTrue(mover.drop());
        assertTrue(fakeBoard.dropCalled);
    }

    @Test
    void hold() {
        mover.hold();
        assertTrue(fakeBoard.holdCalled);
    }

    @Test
    void canMoveDown() {
        fakeBoard.dropResult = false;
        assertFalse(mover.canMoveDown());
        assertTrue(fakeBoard.dropCalled);
    }

    /**
     * A simple fake Board implementation for testing BrickMover.
     * It records whether methods were called and returns preset values.
     */
    static class FakeBoard implements Board {
        boolean moveLeftCalled, moveRightCalled, rotateCalled, dropCalled, holdCalled;
        boolean moveLeftResult, moveRightResult, rotateResult, dropResult;

        @Override
        public boolean moveBrickLeft() {
            moveLeftCalled = true;
            return moveLeftResult;
        }

        @Override
        public boolean moveBrickRight() {
            moveRightCalled = true;
            return moveRightResult;
        }

        @Override
        public boolean rotateLeftBrick() {
            rotateCalled = true;
            return rotateResult;
        }

        @Override
        public boolean moveBrickDown() {
            dropCalled = true;
            return dropResult;
        }

        @Override
        public void holdBrick() {
            holdCalled = true;
        }

        @Override public boolean createNewBrick() { return false; }
        @Override public int[][] getBoardMatrix() { return new int[0][0]; }
        @Override public ViewData getViewData() { return null; }
        @Override public void mergeBrickToBackground() {}
        @Override public ClearRow clearRows() { return null; }
        @Override public Score getScore() { return null; }
        @Override public void newGame() {}
    }
}