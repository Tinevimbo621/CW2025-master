package com.comp2042.audio;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/**
 * Utility class for managing background music playback.
 *
 * Provides methods to initialize, play, and pause looping background audio.
 *
 */

public class SoundManager {
    /** MediaPlayer instance for background music. */

    private static MediaPlayer bgPlayer;

    /**
     * Initializes the background music player with the specified audio file.
     * This should be called once (e.g., from {@code GuiController.initialize}).
     *
     * @param bgPath the path to the background music resource
     */

    public static void initBackground(String bgPath) {
        if (bgPlayer != null) return;
        Media bgMusic = new Media(SoundManager.class.getResource(bgPath).toString());
        bgPlayer = new MediaPlayer(bgMusic);
        bgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        bgPlayer.setVolume(0.5);
    }
    /**
     * Starts or resumes background music playback.
     */
    public static void playBackground() {
        if (bgPlayer != null) bgPlayer.play();
    }
    /**
     * Pauses background music playback.
     */

    public static void pauseBackground() {
        if (bgPlayer != null) bgPlayer.pause();
    }

}