package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the player's score and level progression in the game.
 * <p>
 * Score increases with gameplay actions, and level is calculated based on score thresholds.
 * </p>
 */
public final class Score {
    /**
     * Constructs a new Score instance.
     * <p>
     * Default constructor required for JavaFX.
     * </p>
     */
    public Score() {
        // Default constructor
    }
    /** Points required to advance one level. */
    private static final int POINTS_PER_LEVEL = 1000;
    /** The current score as a JavaFX property. */
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    /** The current level as a JavaFX property. */

    private final IntegerProperty level = new SimpleIntegerProperty(1);
    /**
     * Returns the score property for binding or observation.
     *
     * @return the score property
     */

    public IntegerProperty scoreProperty() {
        return score;
    }
    /**
     * Returns the level property for binding or observation.
     *
     * @return the level property
     */

    public IntegerProperty levelProperty() {
        return level;
    }
    /**
     * Gets the current score value.
     *
     * @return the score
     */

    public int getScore() { return score.get(); }
    /**
     * Gets the current level value.
     *
     * @return the level
     */
    public int getLevel() { return level.get(); }
    /**
     * Adds points to the score and updates the level accordingly.
     *
     * @param points the number of points to add
     */
    public void add(int points){
        score.setValue(score.getValue() + points);
        updateLevel();
    }
    /**
     * Updates the level based on the current score.
     * Level = (score / POINTS_PER_LEVEL) + 1
     */

    public void updateLevel() {
        int newLevel = (score.get() / POINTS_PER_LEVEL) + 1;
        if (newLevel != level.get()) {
            level.setValue(newLevel);
        }
    }
    /**
     * Resets the score and level to their initial values.
     */
    public void reset() {
        score.setValue(0);
        level.setValue(1);
    }



}
