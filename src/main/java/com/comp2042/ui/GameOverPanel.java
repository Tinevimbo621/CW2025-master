package com.comp2042.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A panel displayed when the game ends.
 * <p>
 * Provides options to return to the main menu or view the leaderboard.
 * The panel is initially hidden and should be made visible when the game ends.
 * </p>
 */

public class GameOverPanel extends BorderPane {

    // Styling constants
    private static final String GAME_OVER_STYLE_CLASS = "gameOverStyle";
    private static final String BUTTON_STYLE_CLASS = "pause-button";

    // Layout constants
    private static final double BUTTON_SPACING = 20.0;
    private static final double LAYOUT_SPACING = 30.0;
    private static final Insets LABEL_PADDING = new Insets(20);
    private static final Insets BUTTON_BOX_PADDING = new Insets(20);
    private static final Insets LAYOUT_PADDING = new Insets(40);


    /** Action to execute when navigating to the main menu. */
    private final Runnable onMainMenu;
    /* Action to execute when navigating to the leaderboard. */

    private final Runnable onLeaderboard;
    /**
     * Constructs a {@code GameOverPanel} with the given navigation actions.
     *
     * @param onMainMenu    action to run when "Main Menu" is clicked
     * @param onLeaderboard action to run when "Leaderboard" is clicked
     * @throws IllegalArgumentException if either action is null
     */
    public GameOverPanel(Runnable onMainMenu, Runnable onLeaderboard) {
        validateActions(onMainMenu, onLeaderboard);

        this.onMainMenu = onMainMenu;
        this.onLeaderboard = onLeaderboard;

        initializeUI();
    }

    /**
     * Validates that the provided actions are not null.
     *
     * @param onMainMenu    the main menu action
     * @param onLeaderboard the leaderboard action
     * @throws IllegalArgumentException if either action is null
     */
    private void validateActions(Runnable onMainMenu, Runnable onLeaderboard) {
        if (onMainMenu == null) {
            throw new IllegalArgumentException("onMainMenu action cannot be null");
        }
        if (onLeaderboard == null) {
            throw new IllegalArgumentException("onLeaderboard action cannot be null");
        }
    }

    /**
     * Initializes and configures the UI components.
     */
    private void initializeUI() {
        Label gameOverLabel = createGameOverLabel();
        Button mainMenuButton = createMainMenuButton();
        Button leaderboardButton = createLeaderboardButton();

        HBox buttonBox = createButtonBox(mainMenuButton, leaderboardButton);
        VBox layout = createMainLayout(gameOverLabel, buttonBox);

        setCenter(layout);
        setVisible(false); // Initially hidden
    }

    /**
     * Creates the "GAME OVER" label.
     *
     * @return configured Label
     */
    private Label createGameOverLabel() {
        Label label = new Label("GAME OVER");
        label.getStyleClass().add(GAME_OVER_STYLE_CLASS);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setPadding(LABEL_PADDING);
        return label;
    }

    /**
     * Creates the "Main Menu" button.
     *
     * @return configured Button
     */
    private Button createMainMenuButton() {
        Button button = new Button("Main Menu");
        button.getStyleClass().add(BUTTON_STYLE_CLASS);
        button.setOnAction(e -> safeRun(onMainMenu, "main menu"));
        return button;
    }

    /**
     * Creates the "Leaderboard" button.
     *
     * @return configured Button
     */
    private Button createLeaderboardButton() {
        Button button = new Button("Leaderboard");
        button.getStyleClass().add(BUTTON_STYLE_CLASS);
        button.setOnAction(e -> safeRun(onLeaderboard, "leaderboard"));
        return button;
    }

    /**
     * Creates the horizontal box containing the buttons.
     *
     * @param mainMenuButton    the main menu button
     * @param leaderboardButton the leaderboard button
     * @return configured HBox
     */
    private HBox createButtonBox(Button mainMenuButton, Button leaderboardButton) {
        HBox buttonBox = new HBox(BUTTON_SPACING, mainMenuButton, leaderboardButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(BUTTON_BOX_PADDING);
        return buttonBox;
    }

    /**
     * Creates the main vertical layout containing all components.
     *
     * @param gameOverLabel the game over label
     * @param buttonBox     the button container
     * @return configured VBox
     */
    private VBox createMainLayout(Label gameOverLabel, HBox buttonBox) {
        VBox layout = new VBox(LAYOUT_SPACING, gameOverLabel, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(LAYOUT_PADDING);
        return layout;
    }

    /**
     * Safely executes the given action, logging any exceptions.
     *
     * @param action the action to run
     * @param name   descriptive name of the action for error reporting
     */
    private void safeRun(Runnable action, String name) {
        try {
            if (action != null) {
                action.run();
            } else {
                System.err.println("Warning: Attempted to run null action for " + name);
            }
        } catch (Exception ex) {
            System.err.println("Error navigating to " + name + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Shows the game over panel.
     */
    public void show() {
        setVisible(true);
        setMouseTransparent(false);
        toFront();
    }

    /**
     * Hides the game over panel.
     */
    public void hide() {
        setVisible(false);
        setMouseTransparent(true);
    }
}