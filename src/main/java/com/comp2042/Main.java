package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
/**
 * Entry point for the TetrisJFX application.
 * <p>
 * Loads the main menu from FXML and initializes the primary stage.
 * </p>
 */
public class Main extends Application {
    /**
     * Starts the JavaFX application by setting up the primary stage.
     *
     * @param primaryStage the main application window
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("ui/mainMenu.fxml");
       // ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();


        primaryStage.setTitle("TetrisJFX");
        //making my stage resizable
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(800);
        primaryStage.setMinWidth(900);


        //removed Scene height and width
        Scene scene = new Scene(root,900, 800);
        primaryStage.setScene(scene);
        primaryStage.show();


        //new GameController(c);
    }
    /**
     * Launches the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

