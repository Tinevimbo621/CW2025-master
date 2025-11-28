package com.comp2042.Controller;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.model.DownData;
import com.comp2042.model.ViewData;
import com.comp2042.ui.GameOverPanel;
import com.comp2042.ui.NotificationPanel;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private static final int BRICK_SIZE = 20;
    private static final int BOARD_START_ROW = 2;
    private static final double BRICK_PANEL_Y_OFFSET = -42;
    private static final double GAME_SPEED_MILLIS = 400;
    private static final double GHOST_OPACITY = 0.3;
    private static final int BRICK_CORNER_ARC = 9;
    private static final int NEXT_BRICK_CELL_SIZE = BRICK_SIZE;
    private static final double SCENE_WIDTH = 900.0;
    private static final double SCENE_HEIGHT = 800.0;
    private double currentSpeedMs = GAME_SPEED_MILLIS;   // dynamic falling speed


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

    private Rectangle[][] displayMatrix;
    private InputEventListener eventListener;
    private Rectangle[][] rectangles;
    public GridPane holdPanel;
    private GameController gameController;
    private IntegerProperty timeLeft;
    private Rectangle[][] ghostMatrix;
    private Timeline gameTimeline;
    private Label linesLabel;

    private int totalClearedRows = 0 ;
    private int requiredLinesToClear = 10;
    private Runnable levelCompleteHandler;

    private final BooleanProperty isPaused = new SimpleBooleanProperty();
    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    // Color mapping constants
    private static final Paint[] COLOR_MAP = {
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
    public GameController getGameController() {
        return this.gameController;
    }
    public void initializeTimer(IntegerProperty timeLeftProperty, int seconds){
        this.timeLeft = timeLeftProperty;


        this.gameTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    int current = timeLeft.get();
                    if (current > 0) {
                        timeLeft.set(current - 1);
                    } else {
                        // Time runs out, trigger game over
                        if (this.gameTimeline != null) {
                            this.gameTimeline.stop();
                        }
                        if (this.gameController != null) {
                            this.gameController.handleGameOver();
                        }
                    }
                })
        );

        gameTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    //keyboard input handling
    /**
     * Connects keyboard input to the game panel.
     * Only gamePanel receives key events to avoid focus problems.
     */
    private void setupKeyboardInput() {
        gamePanel.setOnKeyPressed(createKeyEventHandler());
    }
    /**
     * Creates a unified KeyEvent handler controlling all game input.
     * Movement keys: arrows / WASD
     * Q → Hard drop
     * C → Hold piece
     * N → New game
     * @return EventHandler for keyboard input
     */
    private EventHandler<KeyEvent> createKeyEventHandler() {
        return keyEvent -> {
            if (!isPaused.get() && !isGameOver.get()) {
                handleGameplayKeys(keyEvent);
            }
            handleSystemKeys(keyEvent);
        };
    }
    /**
     * Handles gameplay-related key inputs.
     *
     * @param keyEvent The keyboard event
     */
    private void handleGameplayKeys(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();

        if (isMovementKey(code)) {
            handleMovementKey(code, keyEvent);
        }
    }
    /**
     * Checks if a key code represents a movement key.
     *
     * @param code The key code to check
     * @return true if it's a movement key
     */
    private boolean isMovementKey(KeyCode code) {
        return code == KeyCode.LEFT || code == KeyCode.RIGHT ||
                code == KeyCode.UP || code == KeyCode.DOWN ||
                code == KeyCode.A || code == KeyCode.D ||
                    code == KeyCode.W || code == KeyCode.S || code == KeyCode.Q||code == KeyCode.C;
    }

    /**
     * Handles movement keys and consumes the event.
     *
     * @param code The key code
     * @param keyEvent The keyboard event
     */
    private void handleMovementKey(KeyCode code, KeyEvent keyEvent) {
        switch (code) {
            case LEFT, A -> handleLeftMovement();
            case RIGHT, D -> handleRightMovement();
            case UP, W -> handleRotation();
            case DOWN, S -> handleDownMovement();
            case Q -> sendHardDrop();
            case C -> holdCurrentBrick();
        }
        keyEvent.consume();
    }
    /**
     * Sends hard drop event to the GameController and refreshes the view.
     */
    private void sendHardDrop() {
        if (eventListener != null) {
            ViewData data = eventListener.onHardDropEvent(
                    new MoveEvent(EventType.HARD_DROP, EventSource.USER)
            );
            refreshBrick(data);
        }
    }
    /**
     * Holds/swaps the current piece and refreshes UI previews.
     */
    private void holdCurrentBrick() {
        if (eventListener == null) return;
        ViewData view = eventListener.onHoldEvent(
                new MoveEvent(EventType.HOLD, EventSource.USER)
        );
        refreshBrick(view);
        updateNextShapesPreview(view.getNextBricksData());
        updateHeldBrick(view.getHeldBrickData());
    }
    /**
     * Renders the held brick in the hold panel.
     *
     * @param heldMatrix 2D array representing the held brick; non-zero values are drawn as colored blocks.
     */
    public void updateHeldBrick(int[][] heldMatrix) {
        if (holdPanel == null) return;
        holdPanel.getChildren().clear();

        if (heldMatrix == null) return;

        GridPane panel = new GridPane();
        panel.setHgap(2);
        panel.setVgap(2);

        for (int r = 0; r < heldMatrix.length; r++) {
            for (int c = 0; c < heldMatrix[r].length; c++) {
                if (heldMatrix[r][c] != 0) {
                    Rectangle block = new Rectangle(NEXT_BRICK_CELL_SIZE, NEXT_BRICK_CELL_SIZE);
                    block.setFill(getFillColor(heldMatrix[r][c]));
                    panel.add(block, c, r);
                }
            }
        }
        holdPanel.getChildren().add(panel);
    }
    /**
     * Handles left movement.
     */
    private void handleLeftMovement() {
        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
    }
    /**
     * Handles right movement.
     */
    private void handleRightMovement() {
        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
    }
    /**
     * Handles rotation.
     */
    private void handleRotation() {
        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
    }
    /**
     * Handles down movement.
     */
    private void handleDownMovement() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }
    /**
     * Handles system-level keys (like new game).
     *
     * @param keyEvent The keyboard event
     */
    private void handleSystemKeys(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.N) {
            newGame();
        }
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
            initializeDisplayMatrix(boardMatrix);
            initializeGhostMatrix(boardMatrix);
            initializeBrickPanel(brick);
            startGameTimeline();
        } catch (Exception e) {
            handleInitializationError("Failed to initialize game view", e);
        }
    }
    /**
     * Initializes the display matrix for the game board.
     *
     * @param boardMatrix The board matrix
     */
    private void initializeDisplayMatrix(int[][] boardMatrix) {
        displayMatrix = createMatrix(boardMatrix.length, boardMatrix[0].length);
        populateDisplayMatrix(boardMatrix);
    }
    /**
     * Creates a 2D array of rectangles.
     *
     * @param rows Number of rows
     * @param cols Number of columns
     * @return 2D array of rectangles
     */
    private Rectangle[][] createMatrix(int rows, int cols) {
        return new Rectangle[rows][cols];
    }
    /**
     * Populates the display matrix with rectangles and adds them to the game panel.
     *
     * @param boardMatrix The board matrix
     */
    private void populateDisplayMatrix(int[][] boardMatrix) {
        for (int i = BOARD_START_ROW; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = createBrickRectangle();
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - BOARD_START_ROW);
            }
        }
    }
    /**
     * Creates a standard brick rectangle.
     *
     * @return Rectangle with default brick properties
     */
    private Rectangle createBrickRectangle() {
        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        rectangle.setFill(Color.TRANSPARENT);
        return rectangle;
    }
    /**
     * Initializes the ghost matrix for showing ghost pieces.
     *
     * @param boardMatrix The board matrix
     */
    private void initializeGhostMatrix(int[][] boardMatrix) {
        ghostMatrix = createMatrix(boardMatrix.length, boardMatrix[0].length);
        populateGhostMatrix(boardMatrix);
    }
    /**
     * Populates the ghost matrix with semi-transparent rectangles.
     *
     * @param boardMatrix The board matrix
     */
    private void populateGhostMatrix(int[][] boardMatrix) {
        for (int i = BOARD_START_ROW; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle ghostRect = createGhostRectangle();
                ghostMatrix[i][j] = ghostRect;
                gamePanel.add(ghostRect, j, i - BOARD_START_ROW);
            }
        }
    }
    /**
     * Creates a ghost rectangle with reduced opacity.
     *
     * @return Rectangle with ghost properties
     */
    private Rectangle createGhostRectangle() {
        Rectangle ghostRect = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        ghostRect.setFill(Color.TRANSPARENT);
        ghostRect.setOpacity(GHOST_OPACITY);
        return ghostRect;
    }
    /**
     * Initializes the brick panel with the initial brick.
     *
     * @param brick The brick data
     */
    private void initializeBrickPanel(ViewData brick) {
        rectangles = createMatrix(brick.getBrickData().length, brick.getBrickData()[0].length);
        populateBrickPanel(brick);
        positionBrickPanel(brick);
    }
    /**
     * Populates the brick panel with brick rectangles.
     *
     * @param brick The brick data
     */
    private void populateBrickPanel(ViewData brick) {
        forEachCell(brick.getBrickData(), (row, col, value) -> {
            Rectangle rectangle = createColoredRectangle(value);
            rectangles[row][col] = rectangle;
            brickPanel.add(rectangle, col, row);
        });
    }
    /**
     * Creates a rectangle with the specified color.
     *
     * @param colorValue The color index
     * @return Rectangle with the specified color
     */
    private Rectangle createColoredRectangle(int colorValue) {
        Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
        rectangle.setFill(getFillColor(colorValue));
        return rectangle;
    }
    /**
     * Positions the brick panel based on the brick's position.
     *
     * @param brick The brick data
     */
    private void positionBrickPanel(ViewData brick) {
        double x = calculateBrickPanelX(brick);
        double y = calculateBrickPanelY(brick);

        brickPanel.setLayoutX(x);
        brickPanel.setLayoutY(y);
    }
    /**
     * Calculates the X position for the brick panel.
     *
     * @param brick The brick data
     * @return X position
     */
    private double calculateBrickPanelX(ViewData brick) {
        return gamePanel.getLayoutX() +
                brick.getxPosition() * brickPanel.getVgap() +
                brick.getxPosition() * BRICK_SIZE;
    }
    /**
     * Calculates the Y position for the brick panel.
     *
     * @param brick The brick data
     * @return Y position
     */
    private double calculateBrickPanelY(ViewData brick) {
        return BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() +
                brick.getyPosition() * brickPanel.getHgap() +
                brick.getyPosition() * BRICK_SIZE;
    }
    /**
     * Starts the game timeline for automatic movement.
     */
    public void startGameTimeline() {
        if (gameTimeline != null) {
            gameTimeline.stop();
        }

        gameTimeline = new Timeline(new KeyFrame(
                Duration.millis(currentSpeedMs),
                event -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        gameTimeline.setCycleCount(Timeline.INDEFINITE);
        gameTimeline.play();
    }
    public void updateFallingSpeed(int level) {

        double base = GAME_SPEED_MILLIS;
        double factor = 0.90;

        currentSpeedMs = Math.max(60, base * Math.pow(factor, level - 1));

        // restart timeline with new interval
        if (gameTimeline != null) {
            boolean running = gameTimeline.getStatus() == Animation.Status.RUNNING;
            gameTimeline.stop();
            gameTimeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.millis(currentSpeedMs),
                            event -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD)))
            );
            if (running) gameTimeline.play();
        }
    }

    /**
     * Gets the fill color for a given color index.
     *
     * @param colorIndex The color index
     * @return The corresponding Paint color
     */
    private Paint getFillColor(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < COLOR_MAP.length) {
            return COLOR_MAP[colorIndex];
        }
        return COLOR_MAP[COLOR_MAP.length - 1]; // Return default color
    }
    /**
     * Refreshes the brick display with new position data.
     * Updates:
     * brick position
     * brick colors
     *  ghost piece
     * @param brick The updated brick data
     */
    private void refreshBrick(ViewData brick) {
        if (!isPaused.get()) {
            updateBrickPosition(brick);
            updateBrickAppearance(brick);
            updateGhostBrick(brick);
        }
    }
    /**
     * Updates the position of the brick panel.
     *
     * @param brick The brick data
     */
    private void updateBrickPosition(ViewData brick) {
        double x = calculateBrickPanelX(brick);
        double y = calculateBrickPanelY(brick);

        brickPanel.setLayoutX(x);
        brickPanel.setLayoutY(y);
    }
    /**
     * Updates the appearance of the brick rectangles.
     *
     * @param brick The brick data
     */
    private void updateBrickAppearance(ViewData brick) {
        forEachCell(brick.getBrickData(), (row, col, value) ->
                updateRectangleAppearance(value, rectangles[row][col]));
    }
    /**
     * Updates the appearance of a single rectangle.
     *
     * @param colorValue The color value
     * @param rectangle The rectangle to update
     */
    private void updateRectangleAppearance(int colorValue, Rectangle rectangle) {
        rectangle.setFill(getFillColor(colorValue));
        rectangle.setArcHeight(BRICK_CORNER_ARC);
        rectangle.setArcWidth(BRICK_CORNER_ARC);
    }
    /**
     * Updates the ghost brick position and appearance.
     *
     * @param brick The current brick data
     */
    private void updateGhostBrick(ViewData brick) {
        int ghostDistance = calculateGhostDistance(brick);
        drawGhost(brick, ghostDistance);
    }
    /**
     * Calculates the distance for the ghost piece to drop.
     *
     * @param brick The current brick data
     * @return The ghost drop distance
     */
    private int calculateGhostDistance(ViewData brick) {
        return eventListener instanceof GameController
                ? ((GameController) eventListener).getGhostDrop(brick)
                : 0;
    }
    /**
     * Refreshes the game background with updated board data.
     *
     * @param board The updated board matrix
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = BOARD_START_ROW; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                updateRectangleAppearance(board[i][j], displayMatrix[i][j]);
            }
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
     * Handles downward movement and associated game events.
     *
     * @param event The move event
     */
    private void moveDown(MoveEvent event) {
        if (isGameOver.get() ){
            gameController.handleGameOver();
            return;
        }

        if (!isPaused.get()) {
            DownData downData = eventListener.onDownEvent(event);
            handleClearRowEvents(downData);
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }
    /**
     * Handles clear row events and updates UI accordingly.
     *
     * @param downData The down movement data
     */
    private void handleClearRowEvents(DownData downData) {
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
        levelProperty.addListener((obs, oldVal, newVal) -> {
            updateFallingSpeed(newVal.intValue());
        });
    }
    public Label getLevelLabel() {
        return levelLabel;
    }

    /**
     * Handles game over state.
     */
    public void gameOver() {
        gameTimeline.stop();
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
        gameTimeline.play();
    }
    /**
     * Resets the game state to initial values.
     */
    private void resetGameState() {
        gameTimeline.stop();
        gameOverPanel.setVisible(false);
        gameOverPanel.setMouseTransparent(true);
        isPaused.set(false);
        isGameOver.set(false);
    }
    /**
     * Updates the next shape preview panel.
     *
     * @param nextShapesMatrix The matrix for the next shapes
     */
    public void updateNextShapesPreview(int[][][] nextShapesMatrix) {
        nextBricksPanel.getChildren().clear();

        for (int[][] shapeMatrix : nextShapesMatrix) {
            GridPane panel = new GridPane();
            panel.setHgap(2);
            panel.setVgap(2);

            for (int r = 0; r < shapeMatrix.length; r++) {
                for (int c = 0; c < shapeMatrix[r].length; c++) {
                    if (shapeMatrix[r][c] != 0) {
                        Rectangle block = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                        block.setFill(getFillColor(shapeMatrix[r][c]));
                        panel.add(block, c, r);
                    }
                }
            }

            nextBricksPanel.getChildren().add(panel);
        }
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
        gameTimeline.play();
        pauseButton.setText("Pause");
        isPaused.set(false);
    }

    /**
     * Pauses the game.
     */
    private void pauseGameInternal() {
        gameTimeline.pause();
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
    /**
     * Draws the ghost brick at the specified drop position.
     *
     * @param brick The current brick data
     * @param ghostDistance The distance for the ghost to drop
     */
    private void drawGhost(ViewData brick, int ghostDistance) {
        clearGhostMatrix();
        drawGhostBrick(brick, ghostDistance);
    }
    /**
     * Draws the ghost brick at the calculated position
     *@param brick The brick data
     * @param ghostDistance  the ghost drop distance
     */
    private void drawGhostBrick(ViewData brick, int ghostDistance) {
        if (ghostMatrix == null) return;

        int[][] data = brick.getBrickData();
        int x = brick.getxPosition();
        int y = brick.getyPosition() + ghostDistance;

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {

                if (data[row][col] == 0) continue;

                int r = y + row;
                int c = x + col;

                // Bounds check – CRITICAL
                if (r < 0 || r >= ghostMatrix.length) continue;
                if (c < 0 || c >= ghostMatrix[r].length) continue;

                // Null check – prevents all NPEs
                if (ghostMatrix[r][c] != null) {
                    ghostMatrix[r][c].setFill(Color.LIGHTGRAY);
                }
            }
        }
    }

    /**
     * Clears the ghost matrix.
     */
    private void clearGhostMatrix() {
        for (int i = BOARD_START_ROW; i < ghostMatrix.length; i++) {
            for (int j = 0; j < ghostMatrix[i].length; j++) {
                ghostMatrix[i][j].setFill(Color.TRANSPARENT);
            }
        }
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
        if (gameTimeline != null) {
            gameTimeline.stop();
            if (this.timeLeft != null) {
                this.timeLeft.set(seconds);
            }
            gameTimeline.playFromStart();
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
    /**
     * Executes an action for each cell in a matrix
     *
     * @param shape the matrix to a process
     * @param action the action is to apply to each cell
     */
    private void forEachCell(int[][] shape, CellAction action) {
        for (int j = 0; j < shape.length; j++) {
            for (int c = 0; c < shape[j].length; c++) {
                action.apply(j, c, shape[j][c]);
            }
        }
    }


    @FunctionalInterface
    interface CellAction {
        void apply(int row, int col, int value);
    }

}