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


    public ScoreEntry(String playerName, int score, String gameMode) {
        this(playerName, new SimpleIntegerProperty(score), gameMode);
    }

    // Getters
    public String getPlayerName() {
        return playerName;
    }


    public int getScore() {
        return score.get();
    }

    public String getGameMode() {
        return gameMode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }


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