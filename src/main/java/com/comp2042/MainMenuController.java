package com.comp2042;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.geometry.Insets;

import java.awt.*;
import java.net.URL;



public class MainMenuController {
    @FXML
    private ImageView backgroundImage;
    @FXML
    private Button marathonButton;
    @FXML
    private Button sprintButton;
    @FXML
    private Button ultraButton;

    @FXML
    private Button exitButton;


    @FXML
    public void initialize() {

        // Set background image
        backgroundImage.setImage(new Image(getClass().getResource("/Main_menu.jpg").toExternalForm()));

        // Button event handlers
        marathonButton.setOnAction(e -> startMarathon("Marathon"));
        ultraButton.setOnAction(e -> startUltra("Ultra"));
        sprintButton.setOnAction(e -> startSprint("Sprint"));


        exitButton.setOnAction(e -> exitGame());
    }

    private void startMarathon(String mode) {

        try {
            System.out.println("Starting " + mode + " mode...Endless play");

            // Load the actual game layout
            URL gameLayout = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(gameLayout);
            Scene gameScene = new Scene(loader.load(), 800, 800);

            // Get GUI controller
            GuiController guiController = loader.getController();

            // Pass it to GameController
            new GameController(guiController);

            // Switch the stage to the game scene
            Stage stage = (Stage) marathonButton.getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("TetrisJFX - " + mode + " Mode1");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startUltra(String mode) {
        try {
            System.out.println("Starting " + mode + " mode... Timed play (2 minutes)");

            // Load the actual game layout
            URL gameLayout = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(gameLayout);
            Scene gameScene = new Scene(loader.load(), 800, 800);

            // Get GUI controller
            GuiController guiController = loader.getController();

            // Pass it to GameController
            new GameController(guiController);

            // Add countdown timer overlay
            /* // seconds
            Label timerLabel = new Label("Time: " + TIME_LIMIT);
            timerLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-family: \"Let's go Digital\"");*/
            final int TIME_LIMIT = 120;
            final IntegerProperty timeLeft = new SimpleIntegerProperty(TIME_LIMIT);
            final Label timerLabel = new Label();
            timerLabel.textProperty().bind(Bindings.concat("Time: ", timeLeft.asString()));

            StackPane root = (StackPane) gameScene.getRoot();
            root.getChildren().add(timerLabel);
            StackPane.setAlignment(timerLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(timerLabel, new Insets(10, 10, 10, 10));

            final Timeline timer = new Timeline(
                    new KeyFrame(Duration.seconds(1), e -> {
                        int currentTime = timeLeft.get();
                        if (currentTime > 0) {
                            timeLeft.set(currentTime - 1);
                        } else {

                            guiController.gameOver();
                        }
                    })
            );
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();

            // Switch to game scene
            Stage stage = (Stage) ultraButton.getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("TetrisJFX - " + mode + " Mode");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startSprint(String mode) {
        try {
            System.out.println("Starting " + mode + " mode...Endless play");

            // Load the actual game layout
            URL gameLayout = getClass().getClassLoader().getResource("gameLayout.fxml");
            FXMLLoader loader = new FXMLLoader(gameLayout);
            Scene gameScene = new Scene(loader.load(), 800, 800);

            // Get GUI controller
            GuiController guiController = loader.getController();

            // Pass it to GameController
            new GameController(guiController);
            Label LinesLabel = new Label("Lines Cleared: 0");
            LinesLabel.setStyle("-fx-font-size: 25px; -fx-text-fill: black; -fx-font-weight: bold;");


            StackPane root = (StackPane) gameScene.getRoot();
            root.getChildren().add(LinesLabel);
            StackPane.setAlignment(LinesLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(LinesLabel, new Insets(10, 10, 10, 10));

            guiController.setLinesLabel(LinesLabel);

            // Switch the stage to the game scene
            Stage stage = (Stage) sprintButton.getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("TetrisJFX - " + mode + " Mode3");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void exitGame() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

}

