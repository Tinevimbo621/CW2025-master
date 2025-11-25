package com.comp2042.LeaderBoard;

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



/**Generates the leader board table ,loads the entries and allows to go back to main menu after viewing the leader board */
public class LeaderboardController {

    private static final String MAIN_MENU_FXML = "ui/mainMenu.fxml";
    private static final double SCENE_WIDTH = 900.0;
    private static final double SCENE_HEIGHT = 800.0;

    @FXML private TableView<ScoreEntry> table;
    @FXML private TableColumn<ScoreEntry, Number> colRank;
    @FXML private TableColumn<ScoreEntry, String> colName;
    @FXML private TableColumn<ScoreEntry, Number> colScore;
    @FXML private TableColumn<ScoreEntry, String> colDate;

    private LeaderboardManager manager = new LeaderboardManager();

    @FXML
    public void initialize() throws IOException {

        // Rank column (generated via index)
        colRank.setCellValueFactory(cell -> {
            int index = table.getItems().indexOf(cell.getValue()) + 1;
            return new javafx.beans.property.SimpleIntegerProperty(index);
        });

        // Simple mappings to ScoreEntry fields
        colName.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        // Convert LocalDateTime → formatted string
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
     * @param actionEvent The action event
     * @return The current stage
     */
    private Stage getCurrentStage(ActionEvent actionEvent) {
        return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
    }
}
