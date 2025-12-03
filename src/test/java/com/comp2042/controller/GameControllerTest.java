package com.comp2042.controller;

import com.comp2042.LeaderBoard.ScoreEntry;
import com.comp2042.events.EventSource;
import com.comp2042.events.EventType;
import com.comp2042.events.MoveEvent;
import com.comp2042.model.Board;
import com.comp2042.model.DownData;
import com.comp2042.model.SimpleBoard;
import com.comp2042.model.ViewData;
import javafx.beans.property.IntegerProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    private GuiController mockGui;
    private GameController gameController;
    private Board mockBoard;

    @BeforeEach
    void setUp() {
        // Mock all JavaFX and UI-related dependencies
        mockGui = mock(GuiController.class);

        // simple Board implementation
        mockBoard = new SimpleBoard(14, 30) {
            @Override
            public boolean createNewBrick() {
                return false; // prevent actual game loop issues
            }
        };

        // Construct GameController without starting timers/loops
        gameController = new GameController(mockGui, "TestPlayer", "Sprint") {

            protected Board createGameBoard() {
                return mockBoard;
            }

            @Override
            public void startGameLoop() {
                // override to prevent Platform.runLater/timers
            }

            @Override
            public void startGameTimer(IntegerProperty timeLeftProperty) {
                // override to prevent JavaFX timers
            }
        };
    }

    @Test
    void testPlayerNameSanitization() {
        GameController controller = new GameController(mockGui, "  Alice  ", "Sprint") {
            @Override
            public void startGameLoop() {
            }
        };
        ScoreEntry entry = controller.createScoreEntry();
        assertEquals("Alice", entry.getPlayerName());
    }

    @Test
    void testDownEventUserDropIncreasesScore() {
        int initialScore = gameController.board.getScore().getScore();
        DownData data = gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        assertNotNull(data);
        assertTrue(gameController.board.getScore().getScore() > initialScore,
                "Score should increase after user drop");
    }

    @Test
    void testOnLinesClearedAdvancesLevel() {
        gameController.board.getScore().levelProperty().set(1);
        gameController.onLinesCleared(1);
        verify(mockGui, atLeastOnce()).showLevelUpNotification(anyInt());
    }

    @Test
    void testHardDropIncreasesScore() {
        int initialScore = gameController.board.getScore().getScore();
        ViewData view = gameController.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        assertNotNull(view);
        assertTrue(gameController.board.getScore().getScore() > initialScore);
    }

    @Test
    void testHandleGameOverSavesScore() {
        gameController.scoreSaved = false;
        gameController.handleGameOver();
        assertTrue(gameController.scoreSaved);
        verify(mockGui, atLeastOnce()).gameOver();
    }

    @Test
    void testSaveScoreToLeaderboardDoesNotThrow() {
        assertDoesNotThrow(gameController::saveScoreToLeaderboard);
    }

    @Test
    void testGhostDropReturnsNonNegative() {
        int distance = gameController.getGhostDrop(gameController.board.getViewData());
        assertTrue(distance >= 0);
    }

    @Test
    void testMovementEventsReturnViewData() {
        assertNotNull(gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
        assertNotNull(gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
        assertNotNull(gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
        assertNotNull(gameController.onHoldEvent(new MoveEvent(EventType.HOLD, EventSource.USER)));
    }

    @Test
    void testDropDelaysForLevels() {
        int[] expectedDelays = {1000, 900, 800, 700, 600, 500, 400, 300, 200, 100};
        for (int level = 1; level <= expectedDelays.length; level++) {
            int delay = gameController.getDropDelayForLevel(level);
            assertEquals(expectedDelays[level - 1], delay, "Drop delay mismatch at level " + level);
        }
        // Test levels beyond max
        assertEquals(expectedDelays[expectedDelays.length - 1], gameController.getDropDelayForLevel(20));
        // Test level 0
        assertEquals(expectedDelays[0], gameController.getDropDelayForLevel(0));
    }

    @Test
    void testComboScoring_viaHardDrop() {
        int initialScore = gameController.board.getScore().getScore();

        // First hard drop placement + possible line clear -> score increases
        gameController.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        int scoreAfterFirstClear = gameController.board.getScore().getScore();
        assertTrue(scoreAfterFirstClear > initialScore, "Score should increase after first hard drop");

        // Second hard drop directly after  should trigger combo logic if a clear happened
        gameController.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        int scoreAfterSecondClear = gameController.board.getScore().getScore();
        assertTrue(scoreAfterSecondClear > scoreAfterFirstClear, "Score should increase due to combo bonus");
    }


    @Test
    void testLevelAdvancement() {
        // For Sprint mode, level increments when linesCleared >= currentLevel
        int currentLevel = gameController.board.getScore().getLevel();
        for (int i = 0; i < currentLevel; i++) {
            gameController.onLinesCleared(1);
        }

        verify(mockGui, atLeastOnce()).showLevelUpNotification(anyInt());
        assertTrue(gameController.board.getScore().getLevel() >= currentLevel);
    }

    @Test
    void testCreateScoreEntry() {
        ScoreEntry entry = gameController.createScoreEntry();
        assertEquals("TestPlayer", entry.getPlayerName());
        assertEquals("Sprint", entry.getMode());
        assertNotNull(entry.getScore());
    }

    @Test
    void testStartLevelResetsLinesClearedAndTimer() {
        gameController.startLevel();
        // linesClearedThisLevel is private, testing  indirectly
        gameController.onLinesCleared(gameController.board.getScore().getLevel());
        verify(mockGui, atLeastOnce()).showLevelUpNotification(anyInt());
    }

    @Test
    void testHardDropAndGhostDropIntegration() {
        int initialScore = gameController.board.getScore().getScore();
        ViewData view = gameController.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        assertNotNull(view);
        assertTrue(gameController.board.getScore().getScore() > initialScore);
        assertTrue(gameController.getGhostDrop(view) >= 0);
    }

}
