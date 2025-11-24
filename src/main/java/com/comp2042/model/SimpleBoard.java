package com.comp2042.model;

import com.comp2042.LeaderBoard.ClearRow;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private final Score level ;
    private Brick currentBrick;
    private Brick heldBrick = null;          // held piece slot
    private boolean holdUsedThisTurn = false;
    // Queue of next 3 bricks
    private final List<Brick> nextBricks = new ArrayList<>();


    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        level = new Score();
        for (int i = 0; i < 3; i++) {
            nextBricks.add(brickGenerator.getBrick());
        }
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

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

    @Override
    public boolean createNewBrick() {

        //Brick currentBrick = brickGenerator.getBrick();
        //make next brick currenct brick
            Brick newCurrent = nextBricks.remove(0);

        // Add a new generated brick to the queue
        nextBricks.add(brickGenerator.getBrick());

        // Assign active brick
        this.currentBrick = newCurrent;
        brickRotator.setBrick(currentBrick);

        // Starting offset
        currentOffset = new Point(6, 0);

        // Shape for collision testing
        int[][] shape = brickRotator.getCurrentShape();

        // Check if spawning overlaps existing blocks
        boolean conflict = MatrixOperations.intersect(
                currentGameMatrix,
                shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );
        if (!conflict) {
            // Allow hold again for the newly spawned piece
            holdUsedThisTurn = false;
        }

        return conflict; // true = game over
    }


        @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }


   // public ViewData getViewData() {
    //    return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0));
   // }
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


    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();

        nextBricks.clear();
        for (int i = 0; i < 3; i++) {
            nextBricks.add(brickGenerator.getBrick());
        }
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
        if (holdUsedThisTurn) return;
        if (currentBrick == null) return;

        holdUsedThisTurn = true;

        if (heldBrick == null) {
            // First time holding: store current, spawn next
            heldBrick = currentBrick;
            // spawn next piece (createNewBrick uses nextBricks queue)
            // If createNewBrick reports game over, caller should handle it
            createNewBrick();
        } else {
            // Swap current <-> held
            Brick tmp = heldBrick;
            heldBrick = currentBrick;
            currentBrick = tmp;

            // give the new current to the rotator and reset position
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(6, 0);

            // If swapped-in piece collides immediately, it's game over (let caller handle)
            // We can return but createNewBrick isn't called here.
        }
    }

    /**
     * Return the held brick's primary shape matrix for UI preview.
     * null if nothing held.
     */
    public int[][] getHeldBrickData() {
        if (heldBrick == null) return null;
        // assume getShapeMatrix().get(0) is the canonical orientation used for previews
        return heldBrick.getShapeMatrix().get(0);
    }

}
