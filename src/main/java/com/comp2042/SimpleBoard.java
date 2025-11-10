package com.comp2042;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.RandomBrickGenerator;

import java.awt.*;

public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    private final Score level ;
    //next brick
    private Brick nextBrick;


    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
        level = new Score();
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
            Brick currentBrick;

            //  to prevent nulls
        if (nextBrick == null) {
            currentBrick = brickGenerator.getBrick();
        } else {
            currentBrick = nextBrick;
        }
        nextBrick = brickGenerator.getBrick();
        if (currentBrick == null || nextBrick == null) {
            System.err.println("BrickGenerator returned null!");
            return true; // force game over
        }
        // Assign it before intersecting
            brickRotator.setBrick(currentBrick);
            currentOffset = new Point(6, 0); // start higher on the board

            int[][] shape = brickRotator.getCurrentShape();

// Check if the new brick overlaps existing blocks (means game over)
        boolean conflict = MatrixOperations.intersect(
                currentGameMatrix,
                shape,
                (int) currentOffset.getX(),
                (int) currentOffset.getY()
        );

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
       int[][] nextShapeMatrix = (nextBrick != null)
               ? nextBrick.getShapeMatrix().get(0)
               : new int[0][0];

       return new ViewData(
               brickRotator.getCurrentShape(),
               (int) currentOffset.getX(),
               (int) currentOffset.getY(),
               nextShapeMatrix
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
        createNewBrick();
    }



}
