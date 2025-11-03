package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    public NextShapeInfo getNextShape() {
        if(brick == null) return null;
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    public int[][] getCurrentShape() {
        if(brick == null) return null;
        return brick.getShapeMatrix().get(currentShape);
    }

    public void rotate() {
        if (brick != null) {
            currentShape = (currentShape + 1) % brick.getShapeMatrix().size();
        }
    }
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }
    public Brick getBrick() {
        return brick;
    }

}
