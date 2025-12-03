package com.comp2042.controller;

import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.InputEventListener;
import com.comp2042.events.MoveEvent;
import com.comp2042.model.ClearRow;
import com.comp2042.LeaderBoard.LeaderboardManager;
import com.comp2042.LeaderBoard.ScoreEntry;
import com.comp2042.model.Board;
import com.comp2042.model.DownData;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;

/** controller for managing game logic and coordinating between game board and UI
 * Handles game events , score management and game state transactions
 * * Timer management is delegated to GameTimerManager.
 */

public class GameController implements InputEventListener {

    // Game constants
    private static final int BOARD_WIDTH = 14;
    private static final int BOARD_HEIGHT = 30;
    private static final int USER_DROP_SCORE_BONUS = 1;
    private static final String DEFAULT_PLAYER_NAME = "Unknown Player";
    private static final int LEVEL_TIME_LIMIT_SECONDS = 120;
    private static final int HARD_DROP_SCORE_MULTIPLIER = 2;
    private static final int COMBO_SCORE_BONUS = 25;

    // Game mode identifiers
    private static final String SPRINT_MODE = "Sprint";
    private static final String ULTRA_MODE = "Ultra";


    //Game Components
    final Board board ;
    private final BrickMover brickMover;
    private final LeaderboardManager leaderboardManager ;
    private final GuiController viewGuiController;
    private final GhostPieceManager ghostPieceManager;
    private final GameTimerManager timerManager;
    private IntegerProperty levelTimerProperty;

    //Game state variables
    private final String playerName;
    private final String gameMode;
    boolean scoreSaved = false;
    private int comboCount = 0;
    private int currentLevel = 1;
    private int linesClearedThisLevel = 0;
    private boolean levelWon = false;

    // Drop delay configuration for levels
    private static final int[] DROP_DELAYS_MS = {
            1000, 900, 800, 700, 600, 500, 400, 300, 200, 100
    };
    private static final int MAX_LEVEL = DROP_DELAYS_MS.length;

    /**
     * Constructs a new GameController with the specified GUI controller and game settings.
     *
     * @param c The GUI controller for visual updates
     * @param playerName The name of the player
     * @param mode The game mode being played
     * @throws IllegalArgumentException if guiController is null
     */
    public GameController(GuiController c, String playerName, String mode) {
        this.viewGuiController = c;
        this.playerName = sanitizePlayerName(playerName);
        this.gameMode = mode;
        this.board = createGameBoard();
        this.brickMover = new BrickMover(this.board);
        this.leaderboardManager = new LeaderboardManager();
        this.ghostPieceManager = new GhostPieceManager(board);
        this.timerManager = new GameTimerManager();


        this.viewGuiController.setGameController(this);
        viewGuiController.setOnLevelComplete(() ->{
            if (isSprintMode()) {
                handleLevelComplete();
            }
        });
        board.getScore().levelProperty().addListener((obs, oldLevel, newLevel) -> {
            handleAdaptiveSpeed(newLevel.intValue());
        });
        initializeGame();
        startGameLoop();


    }
    // ==================== TIMER MANAGEMENT
    /**
     * Called by GuiController to initialize and start the game timer.
     * @param timeLeftProperty The property to decrement every second.
     */
    public void startGameTimer(IntegerProperty timeLeftProperty) {
        timerManager.startLevelTimer(timeLeftProperty, this::handleTimeExpired);
    }

    /**
     * Handles the event when the level timer expires.
     */
    private void handleTimeExpired() {
        if (isSprintMode() || ULTRA_MODE.equalsIgnoreCase(gameMode)) {
            handleGameOver();
        }
    }
    /**
     * Reset the timer to a specific number of seconds.
     * @param seconds number of seconds to be reset to
     */
    public void resetTimer(int seconds) {
        timerManager.resetLevelTimer(seconds);
    }
    /**
     * Starts the game loop with automatic brick dropping.
     */
    public void startGameLoop() {
        int delay = getDropDelayForLevel(board.getScore().getLevel());
        timerManager.startGameLoop(delay, this::executeAutoDropLogic);
        Platform.runLater(this::executeAutoDropLogic);
    }
    /**
     * Stops the game loop and all timers.
     */
    public void stopGameLoop() {
        timerManager.stopAll();

    }

    /**
     * Pauses the game and all timers.
     */
    public void pauseGame() {
        timerManager.pauseAll();
    }

    /**
     * Resumes the game and all timers.
     */
    public void resumeGame() {
        timerManager.resumeAll();
    }


    /**
     * Updates game loop speed based on current level.
     *
     * @param newLevel The new level
     */
    private void handleAdaptiveSpeed(int newLevel) {
        int newDelayMs = getDropDelayForLevel(newLevel);
        timerManager.updateGameLoopSpeed(newDelayMs, this::executeAutoDropLogic);
    }
    /**
     * Gets the drop delay for a specific level.
     *
     * @param level The level
     * @return Drop delay in milliseconds
     */
    protected int getDropDelayForLevel(int level) {
        int index = Math.min(level, MAX_LEVEL) - 1;
        if (index < 0) return DROP_DELAYS_MS[0];
        return DROP_DELAYS_MS[index];
    }

    /**
     * Executes the automatic drop logic for each game loop tick.
     */
    private void executeAutoDropLogic() {
        onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        viewGuiController.refreshBrick(board.getViewData());
    }

    // ==================== GAME MODE MANAGEMENT ====================
    /**
     * Checks if the current game mode is Sprint.
     *
     * @return true if Sprint mode
     */
    private boolean isSprintMode() {
        return SPRINT_MODE.equalsIgnoreCase(gameMode);
    }

    /**
     * Gets the number of lines required to complete the current level.
     *@return number of lines required for level completion
     */
    private int getRequiredLines() {
        return isSprintMode() ? currentLevel : Integer.MAX_VALUE;
    }
    // ==================== GAME INITIALIZATION ====================
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
            viewGuiController.refreshBrick(board.getViewData());
            bindGameProperties();
            updateNextBrickPreview();
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
//LEVEL MANAGEMENT
    /**
     * Starts or restarts the current level with appropriate setup.
     * In Sprint mode, this resets the level timer and line counters.
     */
    public  void startLevel() {
        if (!isSprintMode()) return;
        this.linesClearedThisLevel = 0;
        this.levelWon = false;

        try {
            resetTimer(LEVEL_TIME_LIMIT_SECONDS);
            viewGuiController.resetLinesCleared();
        } catch (Exception e) {
            handleGameError("Failed to reset level timer", e);
        }
    }

    /**
     * Advances to the next level in Sprint mode.
     *
     * <p>advanceLevel() shows a level up notification, increments the level counter,
     * and starts the new level with fresh timers and counters.</p>
     */
    private void advanceLevel() {
        if (!isSprintMode()) return;
        viewGuiController.showLevelUpNotification(currentLevel);
        currentLevel++;
        startLevel();

    }
    /**
     * Handles level completion logic.
     */
    private void handleLevelComplete() {
        viewGuiController.resetTimer(LEVEL_TIME_LIMIT_SECONDS);
        board.getScore().levelProperty().set(board.getScore().getLevel() + 1);
        viewGuiController.showLevelUpNotification(board.getScore().getLevel());
        viewGuiController.resetLinesCleared();
    }
    /**
     * Called by GuiController whenever rows are cleared on the board.
     *
     * @param linesRemoved Number of lines cleared in that event
     */
    public void onLinesCleared(int linesRemoved) {
        if (levelWon) return;
        this.linesClearedThisLevel += linesRemoved;
        int timeLeft = timerManager.getTimeLeft();

        if (linesClearedThisLevel >= getRequiredLines() && timeLeft > 0) {
            levelWon = true;
            advanceLevel();
        }
    }
    //MOVEMENT HANDLERS
    /**
     * Handles downward movement events and associated game logic.
     *
     * <p>DownData onDownEvent(MoveEvent event) processes both automatic gravity drops and user-initiated
     * soft drops. When movement is blocked, it handles brick placement,
     * row clearing, and game over conditions.</p>
     *
     * @param event the movement event containing source information
     * @return DownData containing updated game state and clear row information
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
        return brickMover.canMoveDown();
    }

    /**
     * Handles successful downward movement  including score bonuses..
     *
     * @param event The move event
     * @return Updated DownData
     */
    private DownData handleSuccessfulMove(MoveEvent event) {
        handleUserDropScore(event);
        return new DownData(null, board.getViewData());
    }

    /**
     * Handles score addition for user-initiated soft drops.
     *
     * @param event the movement event containing source information
     */
    private void handleUserDropScore(MoveEvent event) {
        if (event.getEventSource() == EventSource.USER) {
            board.getScore().add(USER_DROP_SCORE_BONUS);
        }
    }

    /**
     * Handles blocked downward movement when the brick cannot move further.
     * <p>This method processes brick placement, row clearing, score updates,
     * new brick spawning, and game over conditions.</p>
     *
     * @return DownData with clear row information and updated view data
     */
    private DownData handleBlockedMove() {
        board.mergeBrickToBackground();
        ClearRow clearRow = processRowClearing();
        handleComboAndNotifications(clearRow);
        handleBrickPlacementComplete(clearRow);
        refreshGameDisplay();

        return new DownData(clearRow, board.getViewData());
    }

    /**
     * Handles combo count update and displays combo notification.
     * @param clearRow The clear row data
     */
    private void handleComboAndNotifications(ClearRow clearRow) {
        if (hasClearedLines(clearRow)) {
            this.comboCount++;
            int comboBonus = this.comboCount * COMBO_SCORE_BONUS; // Use your constant
            board.getScore().add(comboBonus);

            viewGuiController.updateLinesCleared(clearRow.getLinesRemoved());
            viewGuiController.showComboNotification(comboCount, comboBonus);
        } else {
            this.comboCount = 0;
        }
    }

    /**
     * Processes row clearing and score updates.
     *
     * @return ClearRow information containing details about cleared rows
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
            brickMover.moveLeft();
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
            brickMover.moveRight();
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

            brickMover.rotate();
            return board.getViewData();
        } catch (Exception e) {
            handleGameError("Error during rotation", e);
            return board.getViewData();
        }
    }
    /**
     * Handles hold events.
     *
     * @param event The move event
     * @return Updated ViewData
     */
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        try {
            brickMover.hold();
            ViewData view = board.getViewData();
            viewGuiController.updateHeldBrick(view.getHeldBrickData());
            return view;
        } catch (Exception e) {
            handleGameError("Error during brick hold", e);
            return board.getViewData();
        }
    }

    /**
     * Executes the movement part of a hard drop and updates score.
     */
    private void executeHardDropMovementAndScore(ViewData brick) {
        int dropDistance = getGhostDrop(brick);
        for (int i = 0; i < dropDistance; i++) {
            brickMover.drop();
        }
        board.getScore().add(dropDistance * HARD_DROP_SCORE_MULTIPLIER);
    }

    /**
     * Processes the final steps of brick placement: merge, clear, combo, and new piece spawn.
     * This can reuse the handleBlockedMove logic but tailored for hard drop.
     */
    private void processHardDropPlacementCompletion() {
        board.mergeBrickToBackground();

        ClearRow clearRow = processRowClearing();
        handleComboAndNotifications(clearRow);
        handleBrickPlacementComplete(clearRow);
    }
    /**
     * Handles hard drop events.
     *
     * @param event The move event
     * @return Updated ViewData
     */
    @Override
    public ViewData onHardDropEvent(MoveEvent event) {
        try {
            executeHardDropMovementAndScore(board.getViewData());
            processHardDropPlacementCompletion();

            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            viewGuiController.updateNextShapesPreview(board.getViewData().getNextBricksData());

            return board.getViewData();
        }catch (Exception e) {
            handleGameError("Error during hard drop", e);
            return board.getViewData();
        }
    }
    //Game lifeCycle
    /**
     * Creates a new game instance.
     */
    @Override
    public void createNewGame() {
        try {
            board.newGame();
            this.currentLevel = 1;
            if (isSprintMode()) {
                startLevel();
            }

            if (ULTRA_MODE.equalsIgnoreCase(gameMode)) {
                viewGuiController.resetTimer(LEVEL_TIME_LIMIT_SECONDS);

            }
            viewGuiController.updateNextShapesPreview(board.getViewData().getNextBricksData());
            viewGuiController.updateHeldBrick(board.getViewData().getHeldBrickData());
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } catch (Exception e) {
            handleGameError("Error creating new game", e);
        }
    }
    /**
     * Handles game over state.
     */
   public void handleGameOver() {

       if (!scoreSaved) {
           saveScoreToLeaderboard();
           scoreSaved = true;
       }
        viewGuiController.gameOver();
    }
    //ui updates
    /**
     * Updates the next brick preview in the UI.
     */
    private void updateNextBrickPreview() {
        viewGuiController.updateNextShapesPreview(board.getViewData().getNextBricksData());
    }
    /**
     * Refreshes the game display.
     */
    private void refreshGameDisplay() {
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }

    //LEADERBOARD
    /**
     * Saves the current score to the leaderboard.
     */
    public void saveScoreToLeaderboard() {

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
    protected ScoreEntry createScoreEntry() {
        IntegerProperty finalScore = board.getScore().scoreProperty();
        return new ScoreEntry(playerName, finalScore, gameMode);
    }
    //GHOST PIECE
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
    //ERROR HANDLING
    /**
     * Logs an error with a given context prefix.
     *
     * @param context   the source or category of the error (for example "LEADERBOARD", "INITIALIZATION")
     * @param message   the error message
     * @param exception the exception that occurred, may be null
     */
    private void handleError(String context, String message, Exception exception) {
        System.err.println(context + " ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
    }
    /**
     * Handles game-related errors with logging and recovery.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleGameError(String message, Exception exception) {
        handleError("GAME ERROR:", message, exception);

    }
    /**
     * Handles leaderboard-related errors.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleLeaderboardError(String message, Exception exception) {
        handleError("LEADERBOARD", message, exception);

    }
    /**
     * Handles initialization errors.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleInitializationError(String message, Exception exception) {
        handleError("INITIALIZATION", message, exception);

    }
}
