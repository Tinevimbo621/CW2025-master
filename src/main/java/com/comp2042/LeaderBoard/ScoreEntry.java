package com.comp2042.LeaderBoard;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDateTime;

/**
 * Represents a single entry in the leaderboard.
 * <p>
 * Each entry stores the player's name, score, game mode, and the timestamp
 * when the score was recorded. The score is wrapped in an {@link IntegerProperty}
 * to support JavaFX property binding.
 */
public class ScoreEntry {
    private String playerName;
    private IntegerProperty score;
    private String gameMode;
    private LocalDateTime timestamp;
    /**
     * Constructs a {@code ScoreEntry} with the given player name, score property, and game mode.
     * If the provided score property is {@code null}, a default score of 0 is used.
     *
     * @param playerName the name of the player
     * @param score      the score property (nullable)
     * @param gameMode   the game mode in which the score was achieved
     */

    public ScoreEntry(String playerName, IntegerProperty score, String gameMode) {
        this.playerName = playerName;
        this.score = (score != null) ? score : new SimpleIntegerProperty(0);
        this.gameMode = gameMode;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructs a {@code ScoreEntry} with a primitive score value.
     *
     * @param playerName the name of the player
     * @param score      the score value
     * @param gameMode   the game mode in which the score was achieved
     */

    public ScoreEntry(String playerName, int score, String gameMode) {
        this(playerName, new SimpleIntegerProperty(score), gameMode);
    }

    // Getters
    /**
     * @return the player's name
     */

    public String getPlayerName() {
        return playerName;
    }
    /**
     * @return the player's score as an integer
     */

    public int getScore() {
        return score.get();
    }
    /**
     * Retrieves the game mode associated with this score entry.
     * <p>
     * This method acts as the standard getter for the {@code "mode"} property,
     * ensuring compatibility with the {@code PropertyValueFactory} used in the JavaFX {@code TableColumn}.
     *
     * @return the game mode (e.g., "Sprint", "Marathon","Ultrs") as a string
     */
    public String getMode() {
        return gameMode;
    }
    /**
     * Retrieves the internal game mode field.
     * <p>
     * This method is retained for compatibility with existing internal code that may reference
     * the {@code gameMode} field directly. It serves the same function as {@code getMode()}.
     *
     * @return the game mode as a string
     */
    public String getGameMode() {
        return gameMode;
    }
    /**
     * @return the timestamp when the score entry was created
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    // ---------------- Setters ----------------

    /**
     * Updates the player's name.
     *
     * @param playerName the new player name
     */

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    /**
     * Updates the player's score.
     *
     * @param newScore the new score value
     */

    public void setScore(int newScore) {
        this.score.set(newScore);
    }
    /**
     * Updates the game mode.
     *
     * @param gameMode the new game mode
     */
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }
    /**
     * Updates the timestamp.
     *
     * @param timestamp the new timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * Returns a string representation of the score entry.
     *
     * @return formatted string containing player name, score, game mode, and timestamp
     */

    public String toString() {
        return String.format(
                "Player: %s | Score: %d | Mode: %s | Time: %s",
                playerName,
                score.get(),
                gameMode,
                timestamp.toString()
        );
    }
}