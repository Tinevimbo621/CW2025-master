package com.comp2042.model;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the Game Board interface.
 * <p>
 * Manages the game state including the active brick, held brick,
 * next brick queue, score, and board matrix operations.
 * </p>
 /**
 * A concrete implementation of the Tetris game board.
 *
 * <p>This class manages all core game mechanics including:
 * <ul>
 *   <li>Board state and collision detection</li>
 *   <li>Brick movement and rotation</li>
 *   <li>Next piece queue management (3 pieces)</li>
 *   <li>Hold piece functionality</li>
 *   <li>Row clearing and scoring</li>
 *   <li>Game state management</li>
 * </ul>
 *
 * <p>The board maintains a queue of 3 upcoming bricks, supports hold functionality
 * with turn-based restrictions, and provides comprehensive view data for the UI.
 *  */

public class SimpleBoard implements Board {
   //board dimensions
    private final int width,height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;

    //Game state
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    //Brick management
    private Brick currentBrick;
    private Brick heldBrick = null;
    private boolean holdUsedThisTurn = false;
    private final List<Brick> nextBricks = new ArrayList<>();

    // Constants
    private static final int NEXT_BRICKS_QUEUE_SIZE = 3;
    private static final int DEFAULT_SPAWN_X = 6;

    /**
     * Constructs a new simpleboard with the given dimensions.
     *
     * @param width  board width in cells
     * @param height board height in cells
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[height][width];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        initializeNextBricksQueue();
    }
    /**
     * Initializes the next bricks queue with random bricks.
     *
     * <p>Populates the queue with {@value NEXT_BRICKS_QUEUE_SIZE} random bricks
     * to ensure the game starts with a full preview of upcoming pieces.
     */
    private void initializeNextBricksQueue() {
        for (int i = 0; i < NEXT_BRICKS_QUEUE_SIZE; i++) {
            nextBricks.add(brickGenerator.getBrick());
        }
    }
    /** Attempts to move the active brick down by one row.
     *   @return {@code true} if the brick was successfully moved down;
      *         {@code false} if the move was blocked by collision
     *         */
    @Override
    public boolean moveBrickDown() {
        return moveBrick(0, 1);
    }

    /**
     * Attempts to move the active brick left by one column.
     * @return {@code true} if the brick was successfully moved left;
     *         {@code false} if the move was blocked by collision
         */
    @Override
    public boolean moveBrickLeft() {
        return moveBrick(-1, 0);
    }
    /**
     * Attempts to move the active brick right by one column.
     * @return {@code true} if the brick was successfully moved right;
     *         {@code false} if the move was blocked by collision
     */
    @Override
    public boolean moveBrickRight() {
        return moveBrick(1, 0);
    }
    /**
     * Attempts to move the active brick by the specified offset.
     <p>This is a helper method that performs the common logic for all brick
     * movement operations. It creates a copy of the current game matrix for
     * collision testing, calculates the new position, and checks for conflicts.
     **
     * @param X the horizontal movement offset (negative for left, positive for right)
     * @param Y the vertical movement offset (negative for up, positive for down)
     * @return {@code true} if the brick was successfully moved;
     *         {@code false} if the move was blocked by collision
     */
    private boolean moveBrick(int X, int Y) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);

        // Calculate the new position
        Point newPosition = new Point(currentOffset);
        newPosition.translate(X, Y);

        // Check for collision at the new position
        boolean conflict = MatrixOperations.intersect(
                currentMatrix,
                brickRotator.getCurrentShape(),
                (int) newPosition.getX(),
                (int) newPosition.getY()
        );

        if (conflict) {
            return false;
        } else {
            currentOffset = newPosition;
            return true;
        }
    }
    /**
     * Attempts to rotate the active brick 90 degrees counter-clockwise.
     * @return {@code true} if the brick was successfully rotated;
     *         {@code false} if the rotation was blocked by collision
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }
    /**
     * Creates and spawns a new brick on the board.
     *
     * <p>This method manages the brick queue by removing the first brick from
     * the next bricks queue and adding a newly generated brick to maintain the
     * queue size. The new brick is positioned at the top center of the board.
     *
     * <p>Collision detection is performed to check if the new brick can spawn
     * without overlapping existing bricks. If a collision occurs, this indicates
     * game over.
     *
     * <p>After successfully spawning a new brick, the hold functionality is
     * reset to allow the player to hold this new piece.
     *
     * @return {@code true} if the new brick spawned with a collision (game over);
     *         {@code false} if the brick spawned successfully without collision
     */
    @Override
    public boolean createNewBrick() {

        //make next brick current brick
        Brick newCurrent = nextBricks.remove(0);

        // Add a new generated brick to the queue
        nextBricks.add(brickGenerator.getBrick());

        // Set the new brick as the active brick
        this.currentBrick = newCurrent;
        brickRotator.setBrick(currentBrick);

        // Shape for collision testing
        int[][] shape = brickRotator.getCurrentShape();
        currentOffset = new Point((width -shape[0].length) / 2, 1);

        // Check if spawning overlaps existing blocks
        boolean conflict = MatrixOperations.intersect(
                currentGameMatrix,
                shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
        if (!conflict) {

            holdUsedThisTurn = false;
        }
        return conflict;
    }

    /**
     * Returns the current state of the game board matrix.
     * @return a 2D integer array representing the current board state
     * */
        @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }


    /**
     * Creates and returns comprehensive view data for the UI renderer.
     * @return a ViewData object containing all necessary rendering information
     */
     @Override
   public ViewData getViewData() {
       int[][][] nextShapesMatrix = new int[nextBricks.size()][][];

       for (int i = 0; i < nextBricks.size(); i++) {
           nextShapesMatrix[i] = nextBricks.get(i).getShapeMatrix().get(0);
       }
       int[][] held = getHeldBrickData();
       return new ViewData(
               brickRotator.getCurrentShape(),
               (int) currentOffset.getX(),
               (int) currentOffset.getY(),
               nextShapesMatrix,
               held
       );
   }

   /**
    * Merges the currently active brick into the game board matrix.
    * * <p>mergeBrickToBackground()  is called when the active brick can no longer move .The brick's shape matrix is merged with the
    * current game matrix, making it a permanent part of the board state.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }
    /**
     * Checks for and clears any completed rows on the board.
     *
     * <p>clearRows() scans the entire board matrix for rows that are completely
     * filled with bricks. Completed rows are removed, and all rows above are
     * shifted down to fill the gaps. Empty rows are added at the top.
     *
     * @return a ClearRow object containing the new board matrix and clearing information
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }
    /**
     * Returns the current score object associated with this board.
     * @return the Score object containing current game score and level
     */
     @Override
    public Score getScore() {
        return score;
    }
    /**
        * Resets the board to its initial state for a new game.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
       // Reset the score
        score.reset();

        nextBricks.clear();
        initializeNextBricksQueue();

        // Reset hold functionality
        heldBrick = null;
        holdUsedThisTurn = false;

        createNewBrick();
    }

    /**
     * Hold the current active brick.
     * If no brick held yet -> store current and spawn next.
     * If a brick is held -> swap held with current.
     * Holding twice in same turn is prevented.
     */
    public void holdBrick() {
        // If already used hold this turn, ignore
        if (holdUsedThisTurn || currentBrick == null) return;

        holdUsedThisTurn = true;

        if (heldBrick == null) {
            // First time holding: store current, spawn next
            heldBrick = currentBrick;

            createNewBrick();
        } else {
            // Swap current with held
            Brick tmp = heldBrick;
            heldBrick = currentBrick;
            currentBrick = tmp;

            // Update the rotator with the new current brick
            brickRotator.setBrick(currentBrick);
            // Reset the brick position to default spawn position
            currentOffset = new Point(DEFAULT_SPAWN_X, 0);


        }
    }

    /**
     * Return the held brick's primary shape matrix for UI preview.
     * null if nothing held.
     * @return a 2D integer array representing the held brick's shape matrix,
     *         or null if no brick is held
     */
    public int[][] getHeldBrickData() {
        if (heldBrick == null) return null;

        return heldBrick.getShapeMatrix().get(0);
    }

}
