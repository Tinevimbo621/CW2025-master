package com.comp2042.audio;


import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {

    private static MediaPlayer bgPlayer;

    // Initialize background music once (call from GuiController.initialize)
    public static void initBackground(String bgPath) {
        if (bgPlayer != null) return;
        Media bgMusic = new Media(SoundManager.class.getResource(bgPath).toString());
        bgPlayer = new MediaPlayer(bgMusic);
        bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgPlayer.setVolume(0.5);
    }

    public static void playBackground() {
        if (bgPlayer != null) bgPlayer.play();
    }

    public static void pauseBackground() {
        if (bgPlayer != null) bgPlayer.pause();
    }

}