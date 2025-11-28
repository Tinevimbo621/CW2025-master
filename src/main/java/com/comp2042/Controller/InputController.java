package com.comp2042.Controller;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles all keyboard input for the Tetris game.
 */
public class InputController {
    private final GuiController guiController;
    private InputEventListener eventListener;

    public InputController(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Links the GameController (InputEventListener).
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Main JavaFX key handler.
     */
    public EventHandler<KeyEvent> createKeyEventHandler() {
        return keyEvent -> {
            if (!guiController.isPaused() && !guiController.isGameOver()) {
                handleGameplayKeys(keyEvent);
            }
            handleSystemKeys(keyEvent);
        };
    }

    /**
     * GAMEPLAY KEYS
     */
    private void handleGameplayKeys(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (isMovementKey(code)) {
            handleMovementKey(code);
            keyEvent.consume();
        }
    }

    private boolean isMovementKey(KeyCode code) {
        return switch (code) {
            case LEFT, RIGHT, UP, DOWN,
                 A, D, W, S,
                 Q, C -> true;
            default -> false;
        };
    }

    private void handleMovementKey(KeyCode code) {
        if (eventListener == null) return;

        switch (code) {
            case LEFT, A -> moveLeft();
            case RIGHT, D -> moveRight();
            case UP, W -> rotate();
            case DOWN, S -> softDrop();
            case Q -> hardDrop();
            case C -> holdPiece();
        }
    }

    private void moveLeft() {
        ViewData view = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        guiController.refreshBrick(view);
    }

    private void moveRight() {
        ViewData view = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        guiController.refreshBrick(view);
    }

    private void rotate() {
        ViewData view = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        guiController.refreshBrick(view);
    }

    private void softDrop() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }

    private void hardDrop() {
        ViewData view = eventListener.onHardDropEvent(
                new MoveEvent(EventType.HARD_DROP, EventSource.USER)
        );
        guiController.refreshBrick(view);
    }

    private void holdPiece() {
        ViewData view = eventListener.onHoldEvent(
                new MoveEvent(EventType.HOLD, EventSource.USER)
        );

        guiController.refreshBrick(view);
        guiController.updateNextShapesPreview(view.getNextBricksData());
        guiController.updateHeldBrick(view.getHeldBrickData());
    }


    /**
     * SYSTEM KEYS (always work even when paused)
     */
    private void handleSystemKeys(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.N) {
            guiController.newGame();
        }
    }


    /**
     * DOWN MOVE (shared by gravity + user)
     */
    void moveDown(MoveEvent event) {
        if (eventListener == null) return;

        if (guiController.isGameOver()) {
            guiController.handleGameOver();
            return;
        }

        if (!guiController.isPaused()) {
            DownData downData = eventListener.onDownEvent(event);
            guiController.handleClearRowEvents(downData);
            guiController.refreshBrick(downData.getViewData());
        }

        guiController.requestFocus();
    }
}
