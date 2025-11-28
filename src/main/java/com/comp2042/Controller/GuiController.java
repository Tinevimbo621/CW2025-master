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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
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

    private static final double SCENE_WIDTH = 900.0;
    private static final double SCENE_HEIGHT = 800.0;



    // Font and FXML paths
    private static final String FONT_PATH = "digital.ttf";
    private static final String MAIN_MENU_FXML = "ui/mainMenu.fxml";

  GameOverPanel gameOverPanel = new GameOverPanel(
            () -> {
                try {
                    mainmenuDirect();
                } catch (Exception e) {
                    System.err.println("Failed to open main menu: " + e.getMessage());
                    e.printStackTrace();
                }
            },
            () -> {
                try {
                    leaderBoardDirect();
                } catch (Exception e) {
                    System.err.println("Failed to open leaderboard: " + e.getMessage());
                    e.printStackTrace();
                }
            }
    );

    // add near other UI/game state fields

    private IntegerProperty timeLeftProperty; // injected from MainMenuController



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
    @FXML private Label levelLabel;
    @FXML private Button pauseButton ;
    @FXML private StackPane rootPane;


    private InputEventListener eventListener;
    public GridPane holdPanel;
    private GameController gameController;
    private InputController inputController;
    public GameRenderer renderer;
    private IntegerProperty timeLeft;
    private Label linesLabel;

    private int totalClearedRows = 0 ;
    private int requiredLinesToClear = 10;
    private Runnable levelCompleteHandler;


    final BooleanProperty isPaused = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

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
    public boolean isPaused() {
        return isPaused.get();
    }

    public boolean isGameOver() {
        return isGameOver.get();
    }

    public void requestFocus() {
        gamePanel.requestFocus();
    }

    // Add method to expose refreshBrick (delegates to renderer)
    public void refreshBrick(ViewData data) {
        if (!isPaused.get()) {
            Platform.runLater(() -> {
                int ghostDistance = gameController.getGhostDrop(data);
                renderer.refreshBrick(data, ghostDistance);
            });
        }
    }

    // Add method to expose updateNextShapesPreview
    public void updateNextShapesPreview(int[][][] nextShapes) {
        renderer.updateNextShapesPreview(nextShapes);
    }

    // Add method to expose updateHeldBrick
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


    // Make this public or package-private
    void handleGameOver() {
        if (gameController != null) {
            gameController.handleGameOver();
        }
    }
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
            gamePanel.setOnKeyPressed(inputController.createKeyEventHandler());
            initializeGamePanel();
            setupKeyboardInput();
            initializeGameOverPanel();
            setupVisualEffects();
        } catch (Exception e) {
            handleInitializationError("Failed to initialize GuiController", e);
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

    public void initializeTimer(IntegerProperty timeLeftProperty, int seconds){
        this.timeLeft = timeLeftProperty;
        if (gameController != null) {
            gameController.startGameTimer(timeLeftProperty);
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
        // Apply reflection effect to appropriate components if needed
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
            handleInitializationError("Failed to initialize game view", e);
        }
    }


    /**
     * Sets the lines cleared label for tracking progress.
     *
     * @param label The label to use for displaying lines cleared
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
     * Updates the lines cleared count and display.
     *
     * @param linesRemoved Number of lines removed
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
     * Called by GameController to subscribe to level-complete events.
     */
    public void setOnLevelComplete(Runnable handler) {
        this.levelCompleteHandler = handler;
    }

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
    public Label getLevelLabel() {
        return levelLabel;
    }

    /**
     * Handles game over state.
     */
    public void gameOver() {
        gameController.stopGameLoop();
        gameOverPanel.setVisible(true);
        gameOverPanel.toFront();
        gameOverPanel.setMouseTransparent(false);
        isGameOver.set(true);
    }
    /**
     * Starts a new game.
     */
    public void newGame() {
        resetGameState();
        eventListener.createNewGame();
        gamePanel.requestFocus();

    }
    /**
     * Resets the game state to initial values.
     */
    private void resetGameState() {
        gameOverPanel.setVisible(false);
        gameOverPanel.setMouseTransparent(true);
        isPaused.set(false);
        isGameOver.set(false);
    }

    // Button event handlers
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
     * Pauses the game.
     */
    private void pauseGameInternal() {
        gameController.pauseGame();
        pauseButton.setText("Resume");
        isPaused.set(true);
    }
    /**
     * Navigates back to the main menu.
     *
     * @param actionEvent The action event
     * @throws Exception If loading the main menu fails
     */
    @FXML
    public void mainMenu(ActionEvent actionEvent) throws Exception {
        try {
            loadMainMenu(actionEvent);
        } catch (Exception e) {
            handleInitializationError("Failed to load main menu", e);
            throw e;
        }
    }
    /**
     * Navigates back to the main menu (without parameters for GameOverPanel).
     */
    public void mainmenuDirect() throws Exception {
        try {
            // Create a dummy ActionEvent or use alternative navigation
            Platform.runLater(() -> {
                try {
                    // Get current stage from any UI component
                    Stage currentStage = (Stage) gamePanel.getScene().getWindow();
                    loadMainMenuDirectly(currentStage);
                } catch (Exception e) {
                    handleInitializationError("Failed to load main menu", e);
                }
            });
        } catch (Exception e) {
            handleInitializationError("Failed to load main menu", e);
            throw e;
        }
    }
    /**
     * Loads the main menu directly without requiring an ActionEvent.
     */
    private void loadMainMenuDirectly(Stage stage) throws Exception {
        URL location = getClass().getClassLoader().getResource(MAIN_MENU_FXML);
        if (location == null) {
            throw new IOException("Cannot find main menu FXML: " + MAIN_MENU_FXML);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setScene(scene);
        stage.show();
    }


    public  void leaderboard(ActionEvent event) throws Exception{
       try{
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/leaderboard.fxml"));
           Parent root = loader.load();
           Scene scene = new Scene(root);

           Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
           stage.setScene(scene);
           stage.show();
       } catch (Exception e) {
           handleInitializationError("Failed to load leaderboard", e);
           throw e;
       }
    }

    /**
     * Navigates to the leaderboard screen (without parameters for GameOverPanel).
     */
    public void leaderBoardDirect() throws Exception {
        try {
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/leaderboard.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);

                    Stage stage = (Stage) gamePanel.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    handleInitializationError("Failed to load leaderboard", e);
                }
            });
        } catch (Exception e) {
            handleInitializationError("Failed to load leaderboard", e);
            throw e;
        }
    }


    /**
     * Handles initialization errors gracefully.
     *
     * @param message The error message
     * @param exception The exception that occurred
     */
    private void handleInitializationError(String message, Exception exception) {
        System.err.println("ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
        // Could add user notification here in a real application
    }
    /**
     * Loads and displays the main menu.
     *
     * @param actionEvent The action event
     * @throws Exception If loading fails
     */
    private void loadMainMenu(ActionEvent actionEvent) throws Exception {
        URL location = getClass().getClassLoader().getResource(MAIN_MENU_FXML);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();

        Stage stage = getCurrentStage(actionEvent);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        stage.setScene(scene);
        stage.show();
    }
    /**
     * Gets the current stage from the action event.
     *
     * @param actionEvent The action event
     * @return The current stage
     */
    private Stage getCurrentStage(ActionEvent actionEvent) {
        return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;

    }


    public int getTimeLeft() {
        return timeLeft != null ? timeLeft.get() : Integer.MAX_VALUE;
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


    public void resetLinesCleared() {
        totalClearedRows = 0;
        if (linesLabel != null) {
            linesLabel.setText("Lines Cleared: 0");
        }
    }
    /**
     * Shows combo popup (“Combo xN! (+score)”).
     * @param combo number of lines cleared one after the other
     * @param bonus points to be added to score
     */
    public void showComboNotification(int combo, int bonus) {
        String message = "COMBO x" + combo + "  (+" + bonus + ")";
        NotificationPanel panel = new NotificationPanel(message);

        ObservableList<Node> children = groupNotification.getChildren();
        children.add(panel);

        // Reuse the same animation as level-up
        panel.showScore(children);
    }

    @FunctionalInterface
    interface CellAction {
        void apply(int row, int col, int value);
    }

}