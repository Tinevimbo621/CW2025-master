package com.comp2042.controller;

import com.comp2042.LeaderBoard.LeaderboardManager;
import com.comp2042.LeaderBoard.ScoreEntry;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;



/**
 * Controller for the leaderboard screen.
 * <p>
 * Loads saved score entries into a table and allows the user
 * to return to the main menu.
 */
public class LeaderboardController {
    /**
     * Constructs a new LeaderboardController instance.
     * <p>
     * Default constructor required for JavaFX.
     * </p>
     */
    public LeaderboardController() {
        // Default constructor
    }
    private static final String MAIN_MENU_FXML = "ui/mainMenu.fxml";
    private static final double SCENE_WIDTH = 900.0;
    private static final double SCENE_HEIGHT = 800.0;

    @FXML private TableView<ScoreEntry> table;
    @FXML private TableColumn<ScoreEntry, Number> colRank;
    @FXML private TableColumn<ScoreEntry, String> colName;
    @FXML private TableColumn<ScoreEntry, Number> colScore;
    @FXML private TableColumn<ScoreEntry, String> colMode;
    @FXML private TableColumn<ScoreEntry, String> colDate;

    private LeaderboardManager manager = new LeaderboardManager();
    /**
     * Initializes the leaderboard table by configuring column mappings
     * and loading all stored score entries.
     @throws IOException  for java.io.IOException
     */
    @FXML
    public void initialize() throws IOException {

        // Rank column
        colRank.setCellValueFactory(cell -> {
            int index = table.getItems().indexOf(cell.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index);
        });

        // Simple mappings to ScoreEntry fields
        colName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));
        colMode.setCellValueFactory(new PropertyValueFactory<>("mode"));


        // Convert LocalDateTime to formatted string
        colDate.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getTimestamp().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        )
                ));

        // Load leaderboard entries
        List<ScoreEntry> entries = manager.loadEntries();

        table.setItems(FXCollections.observableArrayList(entries));
    }
    /**
     * Returns the user to the main menu.
     *
     * @param actionEvent the button click event
     * @throws  IOException if main menu fails to load
     */
        @FXML
    private void backToMenu(ActionEvent actionEvent) throws Exception {
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
     * @param actionEvent The action event that triggered the navigation
     * @return The current window stage
     */
    private Stage getCurrentStage(ActionEvent actionEvent) {
        return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    }
}
