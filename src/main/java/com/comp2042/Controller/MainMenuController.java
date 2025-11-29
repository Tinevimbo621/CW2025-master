package com.comp2042.Controller;


import com.comp2042.audio.SoundManager;
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

import java.io.IOException;
import java.net.URL;
/**
 * Controller for the main menu of the game.
 * Handles game mode selection and navigation to different game screens.
 * All scene navigation is delegated to SceneNavigator.
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

    // Scene Navigator
    private SceneNavigator navigator;

    private boolean soundEnabled = true;

     //Constants
    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final String GAME_LAYOUT_PATH = "ui/gameLayout.fxml";
    private static final String BACKGROUND_IMAGE_PATH = "/ui/Main_menu.jpg";
    private static final double SCENE_WIDTH = 800.0;
    private static final double SCENE_HEIGHT = 800.0;
    private static final int ULTRA_TIME_LIMIT_SECONDS = 120;
    private static final Insets LABEL_MARGIN = new Insets(10);

    //GAME MODES
    /**
     * Enum representing different game modes with their configurations
     */
    private enum GameMode {
        MARATHON("Marathon",
                "Endless play.\n\n" +
                        "Goal: Survive as long as possible.\n" +
                        "No timer.\n" +
                        "Lines increase your score.\n\n" +
                        "CONTROLS:\n" +
                        "← → : Move left/right\n" +
                        "↓ : Soft drop\n" +
                        "↑ : Rotate\n" +
                        "Q : Hard drop (instant)\n" +
                        "C : Hold piece\n" +
                        "P : Pause",
                false, false),
        SPRINT("Sprint",
                "Sprint Mode\n\n" +
                        "Goal: Clear lines equal to the level you are in.\n" +
                        "Timer: 120 seconds.\n" +
                        "Clearing lines extends progress.\n\n" +
                        "CONTROLS:\n" +
                        "← → : Move left/right\n" +
                        "↓ : Soft drop\n" +
                        "↑ : Rotate\n" +
                        "Q : Hard drop (instant)\n" +
                        "C : Hold piece\n" +
                        "P : Pause",
                true, true),
        ULTRA("Ultra",
                "Ultra Mode\n\n" +
                        "Score as many points as possible within the time limit.\n" +
                        "This is a fast-scoring challenge.\n" +
                        "Timer: 120 seconds.\n\n" +
                        "CONTROLS:\n" +
                        "← → : Move left/right\n" +
                        "↓ : Soft drop\n" +
                        "↑ : Rotate\n" +
                        "Q : Hard drop (instant)\n" +
                        "C : Hold piece\n" +
                        "P : Pause",
                true, false);

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
            initializeNavigator();
            setupBackgroundImage();
            setupButtonHandlers();
            SoundManager.initBackground("/sounds/background.mp3");
            SoundManager.playBackground();
            soundButton.setText("Sound: ON");
        } catch (Exception e) {
            logError("Failed to initialize MainMenuController", e);
        }
    }
    /**
     * Initializes the SceneNavigator.
     * This must be called before any navigation operations.
     */
    private void initializeNavigator() {
        if (navigator == null) {
            navigator = new SceneNavigator("ui/mainMenu.fxml", "ui/leaderboard.fxml");
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
            if (navigator == null) {
                initializeNavigator();
            }
            showModeInstructions(mode);

            // Using SceneNavigator to load game scene with controller
            SceneNavigator.GameSceneData gameData = navigator.loadGameSceneWithController(
                    GAME_LAYOUT_PATH,
                    SCENE_WIDTH,
                    SCENE_HEIGHT
            );
            GuiController guiController = gameData.getController();
            Scene gameScene = gameData.getScene();

            String playerName = getValidatedPlayerName();
            GameController gameController = new GameController(guiController, playerName, mode.getName());

            applyModeSpecificFeatures(guiController, gameScene, mode, gameController);

            // Switch to game scene
            Stage stage = getCurrentStage();
            stage.setScene(gameScene);
            stage.setTitle("TetrisJFX - " + mode.getName() + " Mode");
            stage.show();
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

    //GAME MODE FEATURES
    /**
     * Applies mode-specific features to the game scene.
     *
     * @param guiController The GUI controller
     * @param gameScene The game scene
     * @param mode The game mode
     */
    private void applyModeSpecificFeatures(GuiController guiController, Scene gameScene, GameMode mode, GameController gameController) {
        StackPane root = (StackPane) gameScene.getRoot();

        if (mode.hasTimer()) {
            setupTimerFeature(guiController, root, gameController);
            guiController.getLevelLabel().setVisible(false);
        }else {
            guiController.getLevelLabel().setVisible(true);
        }
        if (mode.hasLineCounter()) {
            setupLineCounterFeature(guiController, root);
        }
    }
    //Time feature
    /**
     * Sets up the timer feature for timed game modes.
     * @param gameController The game controller
     * @param guiController The GUI controller
     * @param root The root StackPane of the scene
     */
    private void setupTimerFeature(GuiController guiController, StackPane root,GameController gameController) {
        IntegerProperty timeLeft = new SimpleIntegerProperty(ULTRA_TIME_LIMIT_SECONDS);

        Label timerLabel = new Label();
        timerLabel.textProperty().bind(Bindings.concat("Time: ", timeLeft.asString()));
        timerLabel.getStyleClass().add("timer-label");

        StackPane.setAlignment(timerLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(timerLabel, LABEL_MARGIN);
        root.getChildren().add(timerLabel);

        guiController.initializeTimer(timeLeft, ULTRA_TIME_LIMIT_SECONDS);
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
        linesLabel.getStyleClass().add("lines-label");

        StackPane.setAlignment(linesLabel, Pos.BOTTOM_LEFT);
        StackPane.setMargin(linesLabel, LABEL_MARGIN);
        root.getChildren().add(linesLabel);

        guiController.setLinesLabel(linesLabel);
    }
  //Navigation
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
     * Opens the leaderboard screen using SceneNavigator.
     *
     * @param event The action event that triggered this method
     */
    @FXML
    public void openLeaderboard(ActionEvent event) {
        try {

            if (navigator == null) {
                initializeNavigator();
            }

            navigator.goToLeaderboard((Node) event.getSource());
        } catch (Exception e) {
            logError("Failed to open leaderboard", e);
            e.printStackTrace();
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

