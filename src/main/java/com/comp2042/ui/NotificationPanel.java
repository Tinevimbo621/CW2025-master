package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
/**
 * A panel used to display temporary notifications (e.g., bonus points).
 * <p>
 * Shows a glowing text label and animates it with fade and translate transitions
 * before removing itself from the parent node list.
 * </p>
 */
public class NotificationPanel extends BorderPane {
    /**
     * Constructs a {@code NotificationPanel} with the given text.
     *
     * @param text the message to display
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }
    /**
     * Plays the notification animation and removes this panel from the given list
     * once the animation finishes.
     *
     * @param list the parent node list containing this panel
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}
