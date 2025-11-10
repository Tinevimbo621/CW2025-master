package com.comp2042;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;


public class MainMenuController {
    @FXML
   private ImageView backgroundImage ;
    @FXML
    private Button marathonButton ;
    @FXML
    private Button sprintButton ;
    @FXML
    private Button ultraButton ;

    @FXML
    private Button exitButton ;

    @FXML
    public void initialize() {

        // Set background image
        backgroundImage.setImage(new Image(getClass().getResource("/Main_menu.jpg").toExternalForm()));

        // Button event handlers
        marathonButton.setOnAction(e-> startGame("Marathon"));
        ultraButton.setOnAction(e->startGame("Ultra"));
        sprintButton.setOnAction(e->startGame("Sprint"));


        exitButton.setOnAction(e->exitGame());
    }

    private void startGame(String mode) {

            try {
                System.out.println("Starting " + mode + " mode...");

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
                stage.setTitle("TetrisJFX - " + mode + " Mode");
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


