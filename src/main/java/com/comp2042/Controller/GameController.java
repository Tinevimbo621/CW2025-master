package com.comp2042.Controller;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.LeaderBoard.LeaderboardManager;
import com.comp2042.LeaderBoard.ScoreEntry;
import com.comp2042.model.Board;
import com.comp2042.model.DownData;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import javafx.beans.property.IntegerProperty;

/** Controller for managing game logic and coordinating between game board and UI
 * Handles game events , score management and game state transactions
 */

public class GameController implements InputEventListener {

    // Game constants
    private static final int BOARD_WIDTH = 39;
    private static final int BOARD_HEIGHT = 23;
    private static final int USER_DROP_SCORE_BONUS = 1;
    private static final String DEFAULT_PLAYER_NAME = "Unknown Player";

    //increasing game board from 25*10 to 39 * 23 so the block can reach all boarders

    private final Board board ;
    private final LeaderboardManager leaderboardManager ;
    private final GuiController viewGuiController;
    private final GhostPieceManager ghostPieceManager;


    //Game state
    private final String playerName;
    private final String gameMode;


    /**
     * Constructs a new GameController with the specified GUI controller and game settings.
     *
     * @param c The GUI controller for visual updates
     * @param playerName The name of the player
     * @param mode The game mode being played
     */
    public GameController(GuiController c, String playerName, String mode) {
        this.viewGuiController = c;
        this.playerName = sanitizePlayerName(playerName);
        this.gameMode = mode;
        this.board = createGameBoard();
        this.leaderboardManager = new LeaderboardManager();
        this.ghostPieceManager = new GhostPieceManager(board);

        initializeGame();

    }

    /**
     * Creates and initializes the game board.
     *
     * @return Configured game board
     */
    private Board createGameBoard() {
        return new SimpleBoard(BOARD_WIDTH, BOARD_HEIGHT);
    }



    /**
     * Initializes the game state and UI.
     */
    private void initializeGame() {
        try {
            setupGameBoard();
            configureGuiController();
            bindGameProperties();
        } catch (Exception e) {
            handleInitializationError("Failed to initialize game", e);
        }
    }


    /**
     * Sets up the initial game board state.
     */
    private void setupGameBoard() {
        board.createNewBrick();
    }

    /**
     * Configures the GUI controller with event listeners and initial view.
     */
    private void configureGuiController() {
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
    }

    /**
     * Binds game properties to the GUI controller.
     */
    private void bindGameProperties() {
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
    }

    /**
     * Sanitizes and validates the player name.
     *
     * @param name The raw player name
     * @return Sanitized player name
     */
    private String sanitizePlayerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT_PLAYER_NAME;
        }
        return name.trim();
    }

    /**
     * Handles downward movement events and associated game logic.
     *
     * @param event The move event
     * @return DownData containing updated game state
     */

    @Override
    public DownData onDownEvent(MoveEvent event) {
        try {
            if (canMoveDown()) {
                return handleSuccessfulMove(event);
            } else {
                return handleBlockedMove();
            }
        } catch (Exception e) {
            handleGameError("Error during down movement", e);
            return createEmergencyDownData();
        }
    }
/**
        * Checks if the current brick can move down.
            * @return true if movement is possible
     */
    private boolean canMoveDown() {
        return board.moveBrickDown();
    }

    /**
     * Handles successful downward movement.
     *
     * @param event The move event
     * @return Updated DownData
     */
    private DownData handleSuccessfulMove(MoveEvent event) {
        handleUserDropScore(event);
        return new DownData(null, board.getViewData());
    }

    /**
     * Handles score addition for user-initiated drops.
     *
     * @param event The move event
     */
    private void handleUserDropScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(USER_DROP_SCORE_BONUS);
        }
    }

    /**
     * Handles blocked downward movement (brick collision).
     *
     * @return Updated DownData with clear row information
     */
    private DownData handleBlockedMove() {
        board.mergeBrickToBackground();
        ClearRow clearRow = processRowClearing();
        handleBrickPlacementComplete(clearRow);
        refreshGameDisplay();

        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Processes row clearing and score updates.
     *
     * @return ClearRow information
     */
    private ClearRow processRowClearing() {
        ClearRow clearRow = board.clearRows();
        if (hasClearedLines(clearRow)) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        return clearRow;
    }

    /**
     * Checks if any lines were cleared.
     *
     * @param clearRow The clear row data
     * @return true if lines were cleared
     */
    private boolean hasClearedLines(ClearRow clearRow) {
        return clearRow != null && clearRow.getLinesRemoved() > 0;
    }

    /**
     * Handles completion of brick placement.
     *
     * @param clearRow The clear row data
     */
    private void handleBrickPlacementComplete(ClearRow clearRow) {
        if (board.createNewBrick()) {
            handleGameOver();
        } else {
            updateNextBrickPreview();
        }
    }

    /**
     * Handles game over state.
     */
    private void handleGameOver() {
        saveScoreToLeaderboard();
        viewGuiController.gameOver();
    }

    /**
     * Updates the next brick preview in the UI.
     */
    private void updateNextBrickPreview() {
        viewGuiController.updateNextShapePreview(board.getViewData().getNextBrickData());
    }

    /**
     * Refreshes the game display.
     */
    private void refreshGameDisplay() {
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    /**
     * Creates emergency DownData for error recovery.
     *
     * @return Basic DownData with current view
     */
    private DownData createEmergencyDownData() {
        return new DownData(null, board.getViewData());
    }

    /**
     * Handles left movement events.
     *
     * @param event The move event
     * @return Updated ViewData
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        try {
            board.moveBrickLeft();
            return board.getViewData();

    } catch (Exception e) {
        handleGameError("Error during left movement", e);
        return board.getViewData();
    }
    }
    /**
     * Handles right movement events.
     *
     * @param event The move event
     * @return Updated ViewData
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        try {
            board.moveBrickRight();
            return board.getViewData();
        } catch (Exception e) {
            handleGameError("Error during right movement", e);
            return board.getViewData();
        }
    }

    /**
     * Handles rotation events.
     *
     * @param event The move event
     * @return Updated ViewData
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        try {
            board.rotateLeftBrick();
            return board.getViewData();
        } catch (Exception e) {
            handleGameError("Error during rotation", e);
            return board.getViewData();
        }
    }


    /**
     * Creates a new game instance.
     */
    @Override
    public void createNewGame() {
        try {
            board.newGame();
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } catch (Exception e) {
            handleGameError("Error creating new game", e);
        }
    }
    /**
     * Saves the current score to the leaderboard.
     */
    private void saveScoreToLeaderboard() {

      IntegerProperty finalScore =board.getScore().scoreProperty();
        try {
            ScoreEntry entry = createScoreEntry();
            leaderboardManager.saveEntry(entry);
        } catch (Exception e) {
            handleLeaderboardError("Failed to save score to leaderboard", e);
        }
    }

    /**
     * Creates a ScoreEntry from the current game state.
     *
     * @return ScoreEntry with player information
     */
    private ScoreEntry createScoreEntry() {
        IntegerProperty finalScore = board.getScore().scoreProperty();
        return new ScoreEntry(playerName, finalScore, gameMode);
    }

    /**
     * Gets the ghost drop distance for the current brick.
     *
     * @param brick The current brick data
     * @return Distance the ghost piece should drop
     */
    public int getGhostDrop(ViewData brick) {
        try {
            return ghostPieceManager.getGhostDropDistance(brick);
        } catch (Exception e) {
            handleGameError("Error calculating ghost drop distance", e);
            return 0;
        }
    }

    /**
     * Handles game-related errors with logging and recovery.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleGameError(String message, Exception exception) {
        System.err.println("GAME ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
        // Could add more sophisticated error handling here
    }

    /**
     * Handles leaderboard-related errors.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleLeaderboardError(String message, Exception exception) {
        System.err.println("LEADERBOARD ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
        // Could add user notification here
    }
    /**
     * Handles initialization errors.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleInitializationError(String message, Exception exception) {
        System.err.println("INITIALIZATION ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
        // Could add more robust error recovery here
    }
    /**
     * Inner class for managing ghost piece calculations and positioning.
     * Encapsulates all ghost piece related logic.
     */
    private static class GhostPieceManager {
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
         * Finds the maximum distance a shape can drop.
         *
         * @param shape The shape matrix
         * @param x Starting X position
         * @param y Starting Y position
         * @param grid The game grid
         * @return Maximum drop distance
         */
        private int findMaximumDropDistance(int[][] shape, int x, int y, int[][] grid) {
            int maxDrop = 0;

            while (canPlaceShape(shape, x, y + maxDrop + 1, grid)) {
                maxDrop++;
            }

            return maxDrop;
        }

        /**
         * Checks if a shape can be placed at the specified position.
         *
         * @param shape The shape matrix
         * @param x X position
         * @param y Y position
         * @param grid The game grid
         * @return true if placement is valid
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
         * Validates if a shape position is valid.
         *
         * @param row Shape row index
         * @param col Shape column index
         * @param x Shape X position
         * @param y Shape Y position
         * @param grid The game grid
         * @return true if position is valid
         */
        private boolean isValidShapePosition(int row, int col, int x, int y, int[][] grid) {
            int gridY = y + row;
            int gridX = x + col;

            // Check boundaries
            if (isOutOfBounds(gridY, gridX, grid)) {
                return false;
            }

            // Check collision with existing blocks
            return grid[gridY][gridX] == 0;
        }

        /**
         * Checks if grid coordinates are out of bounds.
         *
         * @param y Y coordinate
         * @param x X coordinate
         * @param grid The game grid
         * @return true if coordinates are out of bounds
         */
        private boolean isOutOfBounds(int y, int x, int[][] grid) {
            return y >= grid.length || x < 0 || x >= grid[0].length;
        }
    }
}
