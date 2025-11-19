package com.comp2042.LeaderBoard;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDateTime;

public class ScoreEntry {
    private String playerName;
    private IntegerProperty score;
    private String gameMode;
    private LocalDateTime timestamp;

    public ScoreEntry(String playerName, IntegerProperty score, String gameMode) {
        this.playerName = playerName;
        this.score = (score != null) ? score : new SimpleIntegerProperty(0);
        this.gameMode = gameMode;
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for loading from file (ints)
    public ScoreEntry(String playerName, int score, String gameMode, LocalDateTime timestamp) {
        this.playerName = playerName;
        this.score = new SimpleIntegerProperty(score);
        this.gameMode = gameMode;
        this.timestamp = timestamp;
    }

    // Constructor for new score (int)
    public ScoreEntry(String playerName, int score, String gameMode) {
        this(playerName, new SimpleIntegerProperty(score), gameMode);
    }

    // Getters
    public String getPlayerName() {
        return playerName;
    }


    public int getScore() {
        return score.get();         // <- IMPORTANT
    }

    public IntegerProperty scoreProperty() {
        return score;               // <- REQUIRED BY TABLEVIEW
    }

    public String getGameMode() {
        return gameMode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    // Optional: Setters if needed
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setScore(int newScore) {
        this.score.set(newScore);
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Optional: toString for debugging
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