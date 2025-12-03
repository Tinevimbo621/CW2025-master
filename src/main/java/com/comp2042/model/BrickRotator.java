package com.comp2042.model;

import com.comp2042.logic.bricks.Brick;
/**
 * Handles rotation logic for the game brick.
 *
 * <p>This class keeps track of the current rotation state of a brick
 * and provides utilities to rotate, preview the next rotation, and
 * retrieve the current shape matrix.
 * </p>
 */
public class BrickRotator {
    /**
     * Constructs a new  BrickRotator instance.
     * <p>
     * Default constructor required for JavaFX.
     * </p>
     */
    public BrickRotator() {
        // Default constructor
    }
    private Brick brick;
    private int currentShape = 0;
    /**
     * Returns the next rotation state of the current brick
     * without applying the rotation.
     *
     * @return a {@link NextShapeInfo} containing the next shape matrix
     */
    public NextShapeInfo getNextShape() {
        if(brick == null) return null;
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the matrix of the currently active shape rotation.
     *
     * @return the current rotation matrix, or {@code null} if no brick is set
     */
    public int[][] getCurrentShape() {
        if(brick == null) return null;
        return brick.getShapeMatrix().get(currentShape);
    }
    /**
     * Rotates the brick to the next rotation state.
     * Does nothing if no brick is assigned.
     */
    public void rotate() {
        if (brick != null) {
            currentShape = (currentShape + 1) % brick.getShapeMatrix().size();
        }
    }
    /**
     * Manually sets the current rotation index.
     *
     * @param currentShape the shape rotation index to use
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }
    /**
     * Sets a new brick and resets rotation to the default orientation.
     *
     * @param brick the brick to assign
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
    /**
     * Gets the currently assigned brick.
     *
     * @return the active brick, or {@code null} if none is set
     */
    public Brick getBrick() {
        return brick;
    }

}
