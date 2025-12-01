package com.comp2042.Controller;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import com.comp2042.ui.GameOverPanel;
import com.comp2042.ui.NotificationPanel;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * GuiController handles all rendering, UI updates, keyboard input,
 * game timeline control, and communication with the GameController.
 *
 * Responsibilities include:
 *  - Rendering active/ghost bricks
 *  - Rendering next/held pieces
 *  - Handling keyboard controls
 *  - Animations (notifications, level-up)
 *  - Pause, resume, main menu navigation
 *  - Displaying score + level
 *  - Managing game over UI
 */
public class GuiController implements Initializable {

    // UI Constants
    private static final String FONT_PATH = "digital.ttf";

    // Reflection settings
    private static final double REFLECTION_FRACTION = 0.8;
    private static final double REFLECTION_TOP_OPACITY = 0.9;
    private static final int REFLECTION_TOP_OFFSET = -12;

    //FXML injected UI
    @FXML private GridPane gamePanel;
    @FXML private Group groupNotification;
    @FXML private GridPane brickPanel;
    @FXML private VBox nextBricksPanel;
    @FXML private Label scoreLabel ;
    @FXML private Label levelLabeltext;
    @FXML private Label levelLabel;
    @FXML private Button pauseButton ;
    @FXML private StackPane rootPane;

    //Dependencies
    private InputEventListener eventListener;
    public GridPane holdPanel;
    private GameController gameController;
    private InputController inputController;
    private SceneNavigator navigator;
    public GameRenderer renderer;

    //Game State
    private IntegerProperty timeLeft;
    private Label linesLabel;
    private int totalClearedRows = 0 ;
    private int requiredLinesToClear = 10;
    private Runnable levelCompleteHandler;

    final BooleanProperty isPaused = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    GameOverPanel gameOverPanel = new GameOverPanel(
            () -> navigateToMainMenu(),
            () -> navigateToLeaderboard()
    );
    // Color mapping constants
    static final Paint[] COLOR_MAP = {
            Color.TRANSPARENT,  // 0
            Color.AQUA,         // 1
            Color.BLUEVIOLET,   // 2
            Color.DARKGREEN,    // 3
            Color.YELLOW,       // 4
            Color.RED,          // 5
            Color.BEIGE,        // 6
            Color.BURLYWOOD,    // 7
            Color.WHITE         // default
    };

   //INITIALIZATION
    /**
     * Called automatically by JavaFX after FXML loads.
     * Sets up the game panel, keyboard controls, visual effects,
     * and initializes UI components.
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            inputController = new InputController(this);
            renderer = new GameRenderer(gamePanel, brickPanel,
                    nextBricksPanel, holdPanel,COLOR_MAP);
            navigator = new SceneNavigator("ui/mainMenu.fxml", "ui/leaderboard.fxml");
            gamePanel.setOnKeyPressed(inputController.createKeyEventHandler());
            initializeGamePanel();
            setupKeyboardInput();
            initializeGameOverPanel();
            setupVisualEffects();
        } catch (Exception e) {
            handleError("Failed to initialize GuiController", e);
        }
    }

        /**
         * Loads custom font, sets focus, and prepares game panel.
         */
    private void initializeGamePanel() {
        loadCustomFont();
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
    }
    /**
     * Loads the custom font used in score and level labels.
     */
    private void loadCustomFont() {
        try {
            Font.loadFont(getClass().getClassLoader().getResource(FONT_PATH).toExternalForm(), 38);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load custom font: " + FONT_PATH);
        }
    }
    //keyboard input handling
    /**
     * Connects keyboard input to the game panel.
     * Only gamePanel receives key events to avoid focus problems.
     */
    private void setupKeyboardInput() {
        gamePanel.setOnKeyPressed(inputController.createKeyEventHandler());
    }


    // BRICK + GHOST RENDERING

    /**
     * Initializes the game over panel.
     */
    private void initializeGameOverPanel() {
        rootPane.getChildren().add(gameOverPanel);
        gameOverPanel.toFront();
        gameOverPanel.setVisible(false);
        gameOverPanel.setMouseTransparent(true);
    }
    /**
     * Sets up visual effects for the game UI.
     */
    private void setupVisualEffects() {
        Reflection reflection = createReflectionEffect();
    }
    /**
     * Creates a reflection effect for UI components.
     *
     * @return Configured Reflection effect
     */
    private Reflection createReflectionEffect() {
        Reflection reflection = new Reflection();
        reflection.setFraction(REFLECTION_FRACTION);
        reflection.setTopOpacity(REFLECTION_TOP_OPACITY);
        reflection.setTopOffset(REFLECTION_TOP_OFFSET);
        return reflection;
    }
    //Public Game State Methods
    /**
     * Returns whether the game is currently paused.
     *
     * @return true if the game is paused, false otherwise
     */

    public boolean isPaused() {
        return isPaused.get();
    }
    /**
     * Returns whether the game has reached a game-over state.
     *
     * @return true if the game is over, false otherwise
     */

    public boolean isGameOver() {
        return isGameOver.get();
    }

    /**
     * Requests focus for the game panel so it can receive keyboard input.
     * Called after actions that may steal focus, such as popups or UI updates.
     */

    public void requestFocus() {
        gamePanel.requestFocus();
    }
    /**
     * Retrieves the {@code Label} node that displays the static "Level:" text.
     * <p>
     * This is the decorative or descriptive label often used as a title or prefix for the level value.
     *
     * @return the JavaFX Label component displaying the static "Level" text
     */
    public Label getLevelLabelText() {
        return levelLabeltext;
    }
    /**
     * Retrieves the {@code Label} node that displays the current level number.
     * <p>
     * This is the dynamic text element whose content is typically bound to the game's level property.
     *
     * @return the JavaFX Label component showing the current level value
     */
    public Label getLevelLabel() {
        return levelLabel;
    }
    /**
     * Refreshes the active brick and ghost piece on the board.
     * Delegates rendering to the GameRenderer.
     *
     * @param data the updated brick view model containing shape and position
     */

    public void refreshBrick(ViewData data) {
        if (!isPaused.get()) {
            Platform.runLater(() -> {
                int ghostDistance = gameController.getGhostDrop(data);
                renderer.refreshBrick(data, ghostDistance);
            });
        }
    }

    /**
     * Updates the UI component that displays the next upcoming tetrominoes.
     *
     * @param nextShapes an array of matrices representing the next pieces
     */

    public void updateNextShapesPreview(int[][][] nextShapes) {
        renderer.updateNextShapesPreview(nextShapes);
    }

    /**
     * Updates the display for the currently held brick.
     *
     * @param heldMatrix the matrix representation of the held tetromino
     */

    public void updateHeldBrick(int[][] heldMatrix) {
        renderer.updateHeldBrick(heldMatrix);
    }
    /**
     * Delegates the background refresh to the GameRenderer.
     * @param boardMatrix The current state of the board grid.
     */
    public void refreshGameBackground(int[][] boardMatrix) {
        renderer.refreshGameBackground(boardMatrix);
    }

    /**
     * Initializes the game view with the board matrix and initial brick.
     *
     * @param boardMatrix The game board matrix
     * @param brick The initial brick data
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        try {
            renderer.initializeDisplayMatrix(boardMatrix);
            renderer.initializeGhostMatrix(boardMatrix);
            renderer.initializeBrickPanel(brick);
            Platform.runLater(() -> gamePanel.requestFocus());
        } catch (Exception e) {
            handleError("Failed to initialize game view", e);
        }
    }

    //Game Lifecycle Methods

    public void initializeTimer(IntegerProperty timeLeftProperty, int seconds){
        this.timeLeft = timeLeftProperty;
        if (gameController != null) {
            gameController.startGameTimer(timeLeftProperty);
        }
    }
    /**
     * Reset the timer to 120 seconds. Useful when starting a new level.
     * @param seconds number of seconds to be reset to
     */
    public void resetTimer(int seconds) {
        if (this.gameController != null) {
            this.gameController.resetTimer(seconds);
        }
    }
    /**
     * Starts a completely new game session.
     * Resets UI state, clears game over screens,
     * and asks GameController to create a fresh game instance.
     */

    public void newGame() {
        resetGameState();
        eventListener.createNewGame();
        gamePanel.requestFocus();

    }
    /**
     * Resets UI-related flags and hides the game-over panel
     * in preparation for a new game or restart.
     */
    private void resetGameState() {
        gameOverPanel.setVisible(false);
        gameOverPanel.setMouseTransparent(true);
        isPaused.set(false);
        isGameOver.set(false);
    }
    /**
     * Displays the game-over UI, stops game logic,
     * and prevents further input until the user restarts.
     */
    public void gameOver() {
        gameController.stopGameLoop();
        gameOverPanel.setVisible(true);
        gameOverPanel.toFront();
        gameOverPanel.setMouseTransparent(false);
        isGameOver.set(true);
    }
    /**
     * Delegates game-over handling to the GameController.
     * Called when the active tetromino can no longer be placed.
     */

    void handleGameOver() {
        if (gameController != null) {
            gameController.handleGameOver();
        }
    }
    //Pause/Resume
    /**
     * Handles pause/resume game functionality.
     *
     * @param actionEvent The action event
     */
    @FXML
    public void pauseGame(ActionEvent actionEvent) {
        if (isPaused.get()) {
            resumeGame();
        } else {
            pauseGameInternal();
        }
        gamePanel.requestFocus();
    }
    /**
     * Resumes the game.
     */
    private void resumeGame() {
        gameController.resumeGame();
        pauseButton.setText("Pause");
        isPaused.set(false);
    }

    /**
     * Pauses the game immediately.
     * Stops the game loop and updates the pause button label.
     */

    private void pauseGameInternal() {
        gameController.pauseGame();
        pauseButton.setText("Resume");
        isPaused.set(true);
    }
    /**
     * Resumes gameplay specifically when triggered by keyboard input (key P).
     * Bypasses ActionEvent-based handlers.
     */
    public void resumeGameDirect() {
        resumeGame();
    }
    /**
     * Pauses gameplay specifically when triggered by keyboard input (key P).
     * Bypasses ActionEvent-based handlers.
     */
    public void pauseGameDirect() {
        pauseGameInternal();
    }

    // ==================== LINES CLEARED & SCORING ====================

    /**
     * Assigns the UI label used to display the number of lines cleared.
     *
     * @param label the label to update when lines are removed
     */

    public void setLinesLabel(Label label) {
        this.linesLabel = label;
    }

    /**
     * Handles clear row events and updates UI accordingly.
     *
     * @param downData The down movement data
     */
    void handleClearRowEvents(DownData downData) {
        ClearRow clearRow = downData.getClearRow();
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            updateLinesCleared(clearRow.getLinesRemoved());
            showScoreNotification(clearRow.getScoreBonus());
        }
    }
    /**
     * Updates the total lines-cleared counter and notifies the GameController.
     *
     * @param linesRemoved the number of lines removed in a single move
     */

    void updateLinesCleared(int linesRemoved) {
        totalClearedRows += linesRemoved;
        if (linesLabel != null) {
            linesLabel.setText("Lines Cleared: " + totalClearedRows);
        }
        if (gameController != null) {
            gameController.onLinesCleared(linesRemoved);
        }
        if (totalClearedRows >= requiredLinesToClear  && levelCompleteHandler != null) {
            levelCompleteHandler.run();
        }
    }
    /**
     * Resets the displayed counter for lines cleared to zero,
     * typically when starting a new level or game.
     */

    public void resetLinesCleared() {
        totalClearedRows = 0;
        if (linesLabel != null) {
            linesLabel.setText("Lines Cleared: 0");
        }
    }
    /**
     * Registers a callback invoked when the required number of lines
     * for the current level has been cleared.
     *
     * @param handler the action to run upon level completion
     */

    public void setOnLevelComplete(Runnable handler) {
        this.levelCompleteHandler = handler;
    }
// ==================== NOTIFICATIONS ====================
    /**
     * Shows a score notification for cleared lines.
     *
     * @param scoreBonus The score bonus amount
     */
    private void showScoreNotification(int scoreBonus) {
        NotificationPanel notificationPanel = new NotificationPanel("+" + scoreBonus);
        ObservableList<Node> children = groupNotification.getChildren();
        children.add(notificationPanel);
        notificationPanel.showScore(children);
    }
    /**
     * Shows a level alert for completed levels.
     *
     * @param newLevel The completed level
     */
    public void showLevelUpNotification(int newLevel) {
        int nextLevel = newLevel + 1 ;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Level Complete");
            alert.setHeaderText(null);
            alert.setContentText("You've completed Level " + newLevel + "!. Clear " + nextLevel +"rows to complete next level");
            alert.show();
        });

    }
    /**
     * Displays a popup on screen indicating a combo achievement.
     *
     * @param combo number of consecutive line clears
     * @param bonus score bonus awarded for the combo
     */

    public void showComboNotification(int combo, int bonus) {
        String message = "COMBO x" + combo + "  (+" + bonus + ")";
        NotificationPanel panel = new NotificationPanel(message);

        ObservableList<Node> children = groupNotification.getChildren();
        children.add(panel);

        // Reuse the same animation as level-up
        panel.showScore(children);
    }
    // ==================== NAVIGATION (Delegated to SceneNavigator) ====================
    /**
     * Navigate to main menu from button click.
     */
    @FXML
    public void mainMenu(ActionEvent actionEvent) {
        try {
            navigator.goToMainMenu((Node) actionEvent.getSource());
        } catch (Exception e) {
            handleError("Failed to load main menu", e);
        }
    }
    /**
     * Navigate to leaderboard from button click.
     */
    @FXML
    public void leaderboard(ActionEvent actionEvent) {
        try {
            navigator.goToLeaderboard((Node) actionEvent.getSource());
        } catch (Exception e) {
            handleError("Failed to load leaderboard", e);
        }
    }

    /**
     * Navigates back to the main menu screen programmatically,
     * from game-over actions.
     */
    private void navigateToMainMenu() {
        Platform.runLater(() -> {
            try {
                navigator.goToMainMenu(gamePanel);
            } catch (Exception e) {
                handleError("Failed to navigate to main menu", e);
            }
        });
    }

    /**
     * Navigates to the leaderboard screen programmatically,
     * from game-over actions.
     */

    private void navigateToLeaderboard() {
        Platform.runLater(() -> {
            try {
                navigator.goToLeaderboard(gamePanel);
            } catch (Exception e) {
                handleError("Failed to navigate to leaderboard", e);
            }
        });
    }
// ==================== DEPENDENCY INJECTION & BINDING ====================
    /**
     * Sets the event listener for game events.
     *
     * @param eventListener The event listener
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
        inputController.setEventListener(eventListener);
    }
    /**
     * Binds the score label to the score property.
     *
     * @param scoreProperty The score property to bind
     */
    public void bindScore(IntegerProperty scoreProperty) {
        scoreLabel.textProperty().bind(scoreProperty.asString("%d"));
    }
    /**
     * Binds the level label to the level property.
     *
     * @param levelProperty The level property to bind
     */
    public void bindLevel(IntegerProperty levelProperty) {
            levelLabel.textProperty().bind(levelProperty.asString("%d"));

    }
    /**
     * Injects the GameController dependency so this UI controller
     * can trigger game logic and receive events.
     *
     * @param controller the game controller instance
     */

    public void setGameController(GameController controller) {
        this.gameController = controller;

    }

    // ==================== ERROR HANDLING ====================
    /**
     * Handles initialization errors gracefully.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleError(String message, Exception exception) {
        System.err.println("ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
    }

    // ==================== FUNCTIONAL INTERFACE ====================

    @FunctionalInterface
    interface CellAction {
        void apply(int row, int col, int value);
    }

}