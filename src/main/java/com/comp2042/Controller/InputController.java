package com.comp2042.Controller;

import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
/**
 * Handles all keyboard input for the Tetris game.
 * <p>
 * This controller maps user keystrokes to gameplay actions (movement, rotation,
 * dropping, holding pieces) and system actions (pause, new game).
 * It delegates gameplay events to the {@link InputEventListener} (GameController)
 * and updates the GUI via {@link GuiController}.
 */

public class InputController {
    private final GuiController guiController;
    private InputEventListener eventListener;
    /**
     * Constructs an {@code InputController} bound to the given GUI controller.
     *
     * @param guiController the GUI controller responsible for rendering and game state
     */

    public InputController(GuiController guiController) {
        this.guiController = guiController;
    }

    /**
     * Links the {@link GameController} (via {@link InputEventListener})
     * to receive input events.
     *
     * @param eventListener the listener that handles gameplay events
     */

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Creates the main JavaFX key event handler.
     * <p>
     * This handler processes both gameplay keys (movement, rotation, drop, hold)
     * and system keys (pause, new game).
     *
     * @return a JavaFX {@link EventHandler} for {@link KeyEvent}
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
     * Processes gameplay-related key events (movement, rotation, drop, hold).
     *
     * @param keyEvent the key event triggered by user input
     */

    private void handleGameplayKeys(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (isMovementKey(code)) {
            handleMovementKey(code);
            keyEvent.consume();
        }
    }
    /**
     * Checks whether the given key code corresponds to a gameplay action.
     *
     * @param code the key code
     * @return {@code true} if the key is mapped to gameplay, {@code false} otherwise
     */

    private boolean isMovementKey(KeyCode code) {
        return switch (code) {
            case LEFT, RIGHT, UP, DOWN,
                 A, D, W, S,
                 Q, C -> true;
            default -> false;
        };
    }
    /**
     * Maps a gameplay key to its corresponding action.
     *
     * @param code the key code
     */
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
    /** Moves the active brick left and refreshes the GUI. */
    private void moveLeft() {
        ViewData view = eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        guiController.refreshBrick(view);
    }
    /** Moves the active brick right and refreshes the GUI. */
    private void moveRight() {
        ViewData view = eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        guiController.refreshBrick(view);
    }
    /** Rotates the active brick and refreshes the GUI. */
    private void rotate() {
        ViewData view = eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        guiController.refreshBrick(view);
    }
    /** Performs a soft drop (one row down). */
    private void softDrop() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }
    /** Performs a hard drop (instantly to the bottom). */
    private void hardDrop() {
        ViewData view = eventListener.onHardDropEvent(
                new MoveEvent(EventType.HARD_DROP, EventSource.USER)
        );
        guiController.refreshBrick(view);
    }
    /**
     * Holds the current brick, updates previews, and refreshes the GUI.
     */
    private void holdPiece() {
        ViewData view = eventListener.onHoldEvent(
                new MoveEvent(EventType.HOLD, EventSource.USER)
        );

        guiController.refreshBrick(view);
        guiController.updateNextShapesPreview(view.getNextBricksData());
        guiController.updateHeldBrick(view.getHeldBrickData());
    }
    /**
     * Processes system-level key events (new game, pause).
     *
     * @param keyEvent the key event triggered by user input
     */
    private void handleSystemKeys(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (code == KeyCode.N) {
            guiController.newGame();
        }
        else if (code == KeyCode.P) {
            togglePause();
        }
    }
    /** Toggles the pause state of the game. */
    private void togglePause() {
        if (guiController.isPaused()) {
            guiController.resumeGameDirect();
        } else {
            guiController.pauseGameDirect();
        }
    }
    /**
     * Moves the active brick downward (shared by gravity and user input).
     * <p>
     * Handles row clearing, game over state, and GUI refresh.
     *
     * @param event the move event representing a downward action
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
