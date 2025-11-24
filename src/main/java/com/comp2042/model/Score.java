package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {
  private static final int POINTS_PER_LEVEL = 1000;
    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    public IntegerProperty scoreProperty() {
        return score;
    }
    public IntegerProperty levelProperty() {
        return level;
    }

    public int getScore() { return score.get(); }
    public int getLevel() { return level.get(); }


    public void add(int i){
        score.setValue(score.getValue() + i);
        updateLevel();
    }

    private void updateLevel() {
        // Level = score / 1000 + 1 (minimum level 1)
        int newLevel = (score.get() / POINTS_PER_LEVEL) + 1;
        if (newLevel != level.get()) {
            level.setValue(newLevel);
        }
    }

    public void reset() {
        score.setValue(0);
        level.setValue(1);
    }



}
