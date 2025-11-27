package com.comp2042.Controller;


import com.comp2042.audio.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;



/**
 * Controller for the main menu of the game.
 * Handles game mode selection and navigation to different game screens.
 */

public class MainMenuController {
    //FXML UI Components
    @FXML private ImageView backgroundImage;
    @FXML private Button marathonButton;
    @FXML private Button sprintButton;
    @FXML private Button ultraButton;
    @FXML private Button exitButton;
    @FXML private TextField playerNameField;
    @FXML private Button soundButton;

    private boolean soundEnabled = true;
     //Constants

    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final String GAME_LAYOUT_PATH = "ui/gameLayout.fxml";
    private static final String LEADERBOARD_PATH = "/ui/leaderboard.fxml";
    private static final String BACKGROUND_IMAGE_PATH = "/ui/Main_menu.jpg";
    private static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 800.0;
    private static final int ULTRA_TIME_LIMIT_SECONDS = 120;

    // UI Styling Constants
    private static final String TIMER_LABEL_STYLE = "-fx-font-size: 40px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: \"Let's go Digital\";";
    private static final String LINES_LABEL_STYLE = "-fx-font-size: 25px; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: \"Let's go Digital\";";
    private static final Insets LABEL_MARGIN = new Insets(10);

    //GAME MODES
    /**
     * Enum representing different game modes with their configurations
     */
    private enum GameMode {
        MARATHON("Marathon", "Endless play.\n\nGoal: Survive as long as possible.\nNo timer.\nLines increase your score.", false, false),
        SPRINT("Sprint", "Sprint Mode\n\nGoal: Clear lines equal to the level you are in.\nTimer:1 20 seconds.\nClearing lines extends progress.", true  , true),
        ULTRA("Ultra",  "Ultra Mode\n\nScore as many points as possible within the time limit.\nThis is a fast-scoring challenge.\nTimer: 120 seconds.", true, false);

        private final String name;
        private final String instructions;
        private final boolean hasTimer;
        private final boolean hasLineCounter;

        GameMode(String name, String instructions ,boolean hasTimer, boolean hasLineCounter) {
            this.name = name;
            this.instructions = instructions ;
            this.hasTimer = hasTimer;
            this.hasLineCounter = hasLineCounter;
        }

        public String getName() { return name; }
        public String getInstructions() { return instructions; }
        public boolean hasTimer() { return hasTimer; }
        public boolean hasLineCounter() { return hasLineCounter; }
    }
    //INITIALIZATION
    /**
     * Initializes the controller and sets up UI elements and event handlers.
     */
    @FXML
    public void initialize() {
        try {
            setupBackgroundImage();
            setupButtonHandlers();
            SoundManager.initBackground("/sounds/background.mp3");
            SoundManager.playBackground();
            soundButton.setText("Sound: ON");
        } catch (Exception e) {
            logError("Failed to initialize MainMenuController", e);
        }
    }
    //SOUND CONTROL
    @FXML
    private void toggleSound() {
        soundEnabled = !soundEnabled;
        soundButton.setText(soundEnabled ? "Sound: ON" : "Sound: OFF");

        if (soundEnabled) {
            SoundManager.playBackground();
        } else {
            SoundManager.pauseBackground();
        }
    }
   //UI SETUP
    /**
     * Sets up the background image for the main menu.
     */
    private void setupBackgroundImage() {
        try {
            URL imageUrl = getClass().getResource(BACKGROUND_IMAGE_PATH);
            if (imageUrl != null) {
                backgroundImage.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                logError("Background image not found: " + BACKGROUND_IMAGE_PATH, null);
            }
        } catch (Exception e) {
            logError("Failed to load background image", e);
        }
    }
    /**
     * Sets up event handlers for all buttons in the main menu.
     */
    private void setupButtonHandlers() {
        // Button event handlers
        marathonButton.setOnAction(e -> startGame(GameMode.MARATHON));
        ultraButton.setOnAction(e -> startGame(GameMode.ULTRA));
        sprintButton.setOnAction(e -> startGame(GameMode.SPRINT));
        exitButton.setOnAction(e -> exitGame());
    }
    //START GAME
    /**
     * Starts a new game with the specified mode.
     *
     * @param mode The game mode to start
     */
    private void startGame(GameMode mode) {
        try {
            showModeInstructions(mode);

            FXMLLoader loader = loadGameLayout();
            Scene gameScene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);

            GuiController guiController = loader.getController();
            String playerName = getValidatedPlayerName();
            new GameController(guiController, playerName, mode.getName());

            applyModeSpecificFeatures(guiController, gameScene, mode);
            switchToGameScene(gameScene, mode);

        } catch (Exception e) {
            logError("Failed to start game in " + mode.getName() + " mode", e);
        }
    }
    /**
     * Displays an informational alert with instructions for the specified game mode.
     *
     * @param mode the game mode whose name and instructions are shown in the alert
     */

    private void showModeInstructions(GameMode mode) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(mode.getName() + " Instructions");
        alert.setHeaderText(mode.getName() + " Mode");
        alert.setContentText(mode.getInstructions());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    /**
     * Validates and returns the player name, using default if empty.
     *
     * @return Valid player name
     */
    private String getValidatedPlayerName() {
        String playerName = playerNameField.getText().trim();
        return playerName.isEmpty() ? DEFAULT_PLAYER_NAME : playerName;
    }

    /**
     * Loads the game layout FXML file.
     *
     * @return FXMLLoader instance with the game layout
     * @throws IOException if the layout file cannot be loaded
     */
    private FXMLLoader loadGameLayout() throws IOException {
        URL gameLayout = getClass().getClassLoader().getResource(GAME_LAYOUT_PATH);
        if (gameLayout == null) {
            throw new IOException("Game layout file not found: " + GAME_LAYOUT_PATH);
        }
        return new FXMLLoader(gameLayout);
    }
    //GAME MODE FEATURES
    /**
     * Applies mode-specific features to the game scene.
     *
     * @param guiController The GUI controller
     * @param gameScene The game scene
     * @param mode The game mode
     */
    private void applyModeSpecificFeatures(GuiController guiController, Scene gameScene, GameMode mode) {
        StackPane root = (StackPane) gameScene.getRoot();

        if (mode.hasTimer()) {
            setupTimerFeature(guiController, root);
            guiController.getLevelLabel().setVisible(false);
        }
        if (mode.hasLineCounter()) {
            setupLineCounterFeature(guiController, root);
        }

    }
    //Time feature
    /**
     * Sets up the timer feature for timed game modes.
     *
     * @param guiController The GUI controller
     * @param root The root StackPane of the scene
     */
    private void setupTimerFeature(GuiController guiController, StackPane root) {
        IntegerProperty timeLeft = new SimpleIntegerProperty(ULTRA_TIME_LIMIT_SECONDS);


        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.concat("Time: ", timeLeft.asString()));
        timerLabel.setStyle(TIMER_LABEL_STYLE);

        StackPane.setAlignment(timerLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(timerLabel, LABEL_MARGIN);
        root.getChildren().add(timerLabel);

        guiController.initializeTimer(timeLeft, ULTRA_TIME_LIMIT_SECONDS);
        guiController.getGameController().startLevel();

        guiController.getLevelLabel().setVisible(false);
    }
    // LINE COUNTER FEATURE
    /**
     * Sets up the line counter feature for sprint mode.
     *
     * @param guiController The GUI controller
     * @param root The root StackPane of the scene
     */
    private void setupLineCounterFeature(GuiController guiController, StackPane root) {
        Label linesLabel = new Label("Lines Cleared: 0");
        linesLabel.setStyle(LINES_LABEL_STYLE);

        StackPane.setAlignment(linesLabel, Pos.BOTTOM_LEFT);
        StackPane.setMargin(linesLabel, LABEL_MARGIN);
        root.getChildren().add(linesLabel);

        guiController.setLinesLabel(linesLabel);
    }
    //SCENE SWITCHING
    /**
     * Switches the current stage to the game scene.
     *
     * @param gameScene The game scene to switch to
     * @param mode The game mode for the title
     */
    private void switchToGameScene(Scene gameScene, GameMode mode) {
        Stage stage = getCurrentStage();
        stage.setScene(gameScene);
        stage.setTitle("TetrisJFX - " + mode.getName() + " Mode");
        stage.show();
    }
    /**
     * Gets the current stage from any of the button scenes.
     *
     * @return The current Stage
     */
    private Stage getCurrentStage() {
        return (Stage) marathonButton.getScene().getWindow();
    }
    //OTHER BUTTONS
    /**
     * Exits the game application.
     */
    private void exitGame() {
        try {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            logError("Failed to exit game",e);
        }
    }
    /**
     * Opens the leaderboard screen.
     *
     * @param event The action event that triggered this method
     * @throws IOException if the leaderboard FXML cannot be loaded
     */
    public void openLeaderboard(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(LEADERBOARD_PATH));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logError("Failed to open leaderboard", e);
            throw e;
        }
    }
    //ERROR HANDLING
    /**
     * Logs error messages with optional exception details.
     *
     * @param message The error message
     * @param exception The exception that occurred (can be null)
     */
    private void logError(String message, Exception exception) {
        System.err.println("ERROR: " + message);
        if (exception != null) {
            exception.printStackTrace();
        }
    }


}

