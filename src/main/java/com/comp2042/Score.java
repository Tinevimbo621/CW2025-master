package com.comp2042;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);

    public IntegerProperty scoreProperty() {
        return score;
    }
    public IntegerProperty levelProperty() {
        return level;
    }

    public void add(int i){
        score.setValue(score.getValue() + i);
        updateLevel();
    }

    private void updateLevel() {
        // Level = score / 1000 + 1 (minimum level 1)
        int newLevel = (score.get() / 1000) + 1;
        if (newLevel != level.get()) {
            level.setValue(newLevel);
        }
    }

    public void reset() {
        score.setValue(0);
        level.setValue(1);
    }



}
