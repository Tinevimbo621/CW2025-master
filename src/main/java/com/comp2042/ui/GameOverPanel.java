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
 * </p>
 */

public class GameOverPanel extends BorderPane {

    /** Action to execute when navigating to the main menu. */
    private final Runnable onMainMenu;
    /* Action to execute when navigating to the leaderboard. */

    private final Runnable onLeaderboard;
    /**
     * Constructs a {@code GameOverPanel} with the given navigation actions.
     *
     * @param onMainMenu   action to run when "Main Menu" is clicked
     * @param onLeaderboard action to run when "Leaderboard" is clicked
     */

    public GameOverPanel( Runnable onMainMenu, Runnable onLeaderboard) {


        this.onMainMenu = onMainMenu;
        this.onLeaderboard = onLeaderboard;

             final Label gameOverLabel = new Label("GAME OVER");
             gameOverLabel.getStyleClass().add("gameOverStyle");
             gameOverLabel.setMaxWidth(Double.MAX_VALUE);
             gameOverLabel.setAlignment(Pos.CENTER);
             gameOverLabel.setPadding(new Insets(20));



             Button mainMenuButton = new Button("Main Menu");
             mainMenuButton.getStyleClass().add("pause-button");
        mainMenuButton.setOnAction(e ->safeRun(onMainMenu, "main menu"));


        Button LeaderBoardButton = new Button("Leader Board");
             LeaderBoardButton.getStyleClass().add("pause-button");
         LeaderBoardButton.setOnAction(e ->safeRun(onLeaderboard,"leaderboard"));


             // Button container
             HBox buttonBox = new HBox(20, mainMenuButton, LeaderBoardButton);
             buttonBox.setAlignment(Pos.CENTER);
             buttonBox.setPadding(new Insets(20));

             // Main layout
             VBox layout = new VBox(30, gameOverLabel, buttonBox);
             layout.setAlignment(Pos.CENTER);
             layout.setPadding(new Insets(40));

             setCenter(layout);
             setVisible(false); // Initially hidden


         }
    /**
     * Safely executes the given action, logging any exceptions.
     *
     * @param action the action to run
     * @param name   descriptive name of the action for error reporting
     */

    private void safeRun(Runnable action, String name) {
        try {
            action.run();
        } catch (Exception ex) {
            System.err.println("Error navigating to " + name + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
