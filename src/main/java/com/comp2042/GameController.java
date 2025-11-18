package com.comp2042;

import javafx.beans.property.IntegerProperty;

public class GameController implements InputEventListener {
//increasing game board from 25*10 to 39 * 23 so the block can reach all boarders
    private Board board = new SimpleBoard(39, 23);
    private LeaderboardManager leaderboardManager = new LeaderboardManager();
    private String playerName;
    private String gameMode;


    private final GuiController viewGuiController;

    public GameController(GuiController c, String playerName, String mode) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());

            }



            if (board.createNewBrick()) {
                //save the score before ending game
                saveScoreToLeaderboard();
                viewGuiController.gameOver();
            } else {
                viewGuiController.updateNextShapePreview(board.getViewData().getNextBrickData());
            }
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
    }
    // method to save score to leaderboard
    private void saveScoreToLeaderboard() {
      IntegerProperty finalScore =board.getScore().scoreProperty();
        ScoreEntry entry = new ScoreEntry(playerName, finalScore, gameMode);

        try {
            leaderboardManager.saveEntry(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



