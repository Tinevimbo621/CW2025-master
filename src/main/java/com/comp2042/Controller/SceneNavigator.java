package com.comp2042.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Responsible for switching scenes (main menu, leaderboard, game, etc.).
 * Centralized navigation handler to avoid duplicate navigation logic.
 */
public class SceneNavigator {

    private static final double SCENE_WIDTH = 900.0;
    private static final double SCENE_HEIGHT = 800.0;

    private final String mainMenuFxml;
    private final String leaderboardFxml;

    public SceneNavigator(String mainMenuFxml, String leaderboardFxml) {
        this.mainMenuFxml = mainMenuFxml;
        this.leaderboardFxml = leaderboardFxml;
    }

    /**
     * Navigate to main menu from any Node in the current scene.
     * @param sourceNode Any UI node from the current scene
     * @throws IOException if FXML loading fails
     */
    public void goToMainMenu(Node sourceNode) throws IOException {
        Stage stage = stageFromNode(sourceNode);
        goToMainMenu(stage);
    }

    /**
     * Navigate to main menu using a Stage directly.
     * @param stage The stage to load the scene into
     * @throws IOException if FXML loading fails
     */
    public void goToMainMenu(Stage stage) throws IOException {
        loadScene(stage, mainMenuFxml, SCENE_WIDTH, SCENE_HEIGHT);
    }

    /**
     * Navigate to leaderboard from any Node in the current scene.
     * @param sourceNode Any UI node from the current scene
     * @throws IOException if FXML loading fails
     */
    public void goToLeaderboard(Node sourceNode) throws IOException {
        Stage stage = stageFromNode(sourceNode);
        goToLeaderboard(stage);
    }

    /**
     * Navigate to leaderboard using a Stage directly.
     * @param stage The stage to load the scene into
     * @throws IOException if FXML loading fails
     */
    public void goToLeaderboard(Stage stage) throws IOException {
        loadScene(stage, leaderboardFxml, -1, -1);
    }
    /**
     * Loads and returns a game scene without switching to it.
     * Used when the caller needs to configure the scene before displaying.
     *
     * @param gameLayoutPath Path to the game layout FXML
     * @param width Scene width
     * @param height Scene height
     * @return Configured Scene with loaded FXML
     * @throws IOException if FXML loading fails
     */
    public Scene loadGameScene(String gameLayoutPath, double width, double height) throws IOException {
        URL location = resolveResource(gameLayoutPath);
        if (location == null) {
            throw new IOException("Cannot find FXML: " + gameLayoutPath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        return new Scene(root, width, height);
    }

    /**
     * Loads game scene and returns both the scene and its controller.
     *
     * @param gameLayoutPath Path to the game layout FXML
     * @param width Scene width
     * @param height Scene height
     * @return GameSceneData containing the scene and controller
     * @throws IOException if FXML loading fails
     */
    public GameSceneData loadGameSceneWithController(String gameLayoutPath, double width, double height) throws IOException {
        URL location = resolveResource(gameLayoutPath);
        if (location == null) {
            throw new IOException("Cannot find FXML: " + gameLayoutPath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        GuiController controller = loader.getController();

        return new GameSceneData(scene, controller);
    }

    /**
     * Data class to hold scene and controller together.
     */
    public static class GameSceneData {
        private final Scene scene;
        private final GuiController controller;

        public GameSceneData(Scene scene, GuiController controller) {
            this.scene = scene;
            this.controller = controller;
        }

        public Scene getScene() {
            return scene;
        }

        public GuiController getController() {
            return controller;
        }
    }

    /**
     * Loads a scene from an FXML file into the given stage.
     * @param stage The stage to load into
     * @param fxmlPath Path to the FXML file
     * @param width Scene width (use -1 for default)
     * @param height Scene height (use -1 for default)
     * @throws IOException if FXML file cannot be loaded
     */
    private void loadScene(Stage stage, String fxmlPath, double width, double height) throws IOException {
        URL location = getClass().getClassLoader().getResource(fxmlPath);
        if (location == null) {
            throw new IOException("Cannot find FXML: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(location);
        Parent root = loader.load();
        Scene scene = (width > 0 && height > 0)
                ? new Scene(root, width, height)
                : new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Resolves a resource path, trying both with and without leading slash.
     * @param path The resource path
     * @return URL of the resource, or null if not found
     */
    private URL resolveResource(String path) {
        // Try with leading slash (absolute from classpath root)
        URL resource = getClass().getResource(path);
        if (resource != null) {
            return resource;
        }

        // Try without leading slash (relative to package)
        resource = getClass().getClassLoader().getResource(path);
        if (resource != null) {
            return resource;
        }

        // If path doesn't start with /, try adding it
        if (!path.startsWith("/")) {
            resource = getClass().getResource("/" + path);
            if (resource != null) {
                return resource;
            }
        }

        // If path starts with /, try removing it
        if (path.startsWith("/")) {
            resource = getClass().getClassLoader().getResource(path.substring(1));
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    /**
     * Extracts the Stage from any JavaFX Node.
     * @param anyNode Any node in the scene graph
     * @return The Stage containing this node
     */
    public Stage stageFromNode(Node anyNode) {
        return (Stage) anyNode.getScene().getWindow();
    }
}