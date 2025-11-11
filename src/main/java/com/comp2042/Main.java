package com.comp2042;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.JFrame;
import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("mainMenu.fxml");
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


    public static void main(String[] args) {
        launch(args);
    }

}

