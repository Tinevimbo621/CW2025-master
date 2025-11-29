package com.comp2042.Controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.util.Duration;

/**
 * Manages all game timers including the level countdown timer and game loop timer.
 * Provides centralized control for starting, stopping, pausing, and resuming timers.
 */
public class GameTimerManager {

    private Timeline levelTimer;
    private Timeline gameLoopTimer;
    private IntegerProperty timeLeftProperty;
    private Runnable onTimeExpiredCallback;

    /**
     * Starts the level countdown timer.
     * The timer decrements every second until it reaches zero.
     *
     * @param timeLeftProperty The property to bind and decrement
     * @param onTimeExpired Callback to execute when timer reaches zero
     */
    public void startLevelTimer(IntegerProperty timeLeftProperty, Runnable onTimeExpired) {
        this.timeLeftProperty = timeLeftProperty;
        this.onTimeExpiredCallback = onTimeExpired;

        stopLevelTimer();

        levelTimer = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        event -> {
                            if (timeLeftProperty.get() > 0) {
                                timeLeftProperty.set(timeLeftProperty.get() - 1);
                            } else {
                                stopLevelTimer();
                                if (onTimeExpired != null) {
                                    onTimeExpired.run();
                                }
                            }
                        }
                )
        );
        levelTimer.setCycleCount(Timeline.INDEFINITE);
        levelTimer.play();
    }

    /**
     * Resets the level timer to a specific number of seconds and restarts it.
     *
     * @param seconds Number of seconds to reset to
     */
    public void resetLevelTimer(int seconds) {
        if (timeLeftProperty != null) {
            timeLeftProperty.set(seconds);
            if (levelTimer != null) {
                stopLevelTimer();
                startLevelTimer(timeLeftProperty, onTimeExpiredCallback);
            }
        }
    }

    /**
     * Starts the game loop timer that triggers automatic brick drops.
     *
     * @param delayMs Delay in milliseconds between each drop
     * @param onTick Callback to execute on each timer tick
     */
    public void startGameLoop(int delayMs, Runnable onTick) {
        stopGameLoop();

        gameLoopTimer = new Timeline(
                new KeyFrame(
                        Duration.millis(delayMs),
                        event -> {
                            if (onTick != null) {
                                onTick.run();
                            }
                        }
                )
        );
        gameLoopTimer.setCycleCount(Timeline.INDEFINITE);
        gameLoopTimer.play();
    }

    /**
     * Updates the game loop speed by changing the delay between ticks.
     *
     * @param newDelayMs New delay in milliseconds
     * @param onTick Callback to execute on each timer tick
     */
    public void updateGameLoopSpeed(int newDelayMs, Runnable onTick) {
        stopGameLoop();
        startGameLoop(newDelayMs, onTick);
    }

    /**
     * Stops the level countdown timer.
     */
    public void stopLevelTimer() {
        if (levelTimer != null) {
            levelTimer.stop();
        }
    }

    /**
     * Stops the game loop timer.
     */
    public void stopGameLoop() {
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
    }

    /**
     * Stops all timers (both level timer and game loop).
     */
    public void stopAll() {
        stopLevelTimer();
        stopGameLoop();
    }

    /**
     * Pauses the level countdown timer.
     */
    public void pauseLevelTimer() {
        if (levelTimer != null) {
            levelTimer.pause();
        }
    }

    /**
     * Pauses the game loop timer.
     */
    public void pauseGameLoop() {
        if (gameLoopTimer != null) {
            gameLoopTimer.pause();
        }
    }

    /**
     * Pauses all timers.
     */
    public void pauseAll() {
        pauseLevelTimer();
        pauseGameLoop();
    }

    /**
     * Resumes the level countdown timer.
     */
    public void resumeLevelTimer() {
        if (levelTimer != null) {
            levelTimer.play();
        }
    }

    /**
     * Resumes the game loop timer.
     */
    public void resumeGameLoop() {
        if (gameLoopTimer != null) {
            gameLoopTimer.play();
        }
    }

    /**
     * Resumes all timers.
     */
    public void resumeAll() {
        resumeLevelTimer();
        resumeGameLoop();
    }

    /**
     * Checks if the level timer is currently running.
     *
     * @return true if level timer is running
     */
    public boolean isLevelTimerRunning() {
        return levelTimer != null && levelTimer.getStatus() == Timeline.Status.RUNNING;
    }

    /**
     * Checks if the game loop timer is currently running.
     *
     * @return true if game loop is running
     */
    public boolean isGameLoopRunning() {
        return gameLoopTimer != null && gameLoopTimer.getStatus() == Timeline.Status.RUNNING;
    }

    /**
     * Gets the current time left from the property.
     *
     * @return Time left in seconds, or Integer.MAX_VALUE if no timer is set
     */
    public int getTimeLeft() {
        return timeLeftProperty != null ? timeLeftProperty.get() : Integer.MAX_VALUE;
    }
}