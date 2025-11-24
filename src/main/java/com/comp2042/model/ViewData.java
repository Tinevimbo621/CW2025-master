package com.comp2042.model;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][][] nextBricksData;

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][][] nextBricksData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBricksData = nextBricksData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][][] getNextBricksData() {
        int[][][] copy = new int[nextBricksData.length][][];
        for (int i = 0; i < nextBricksData.length; i++) {
            copy[i] = MatrixOperations.copy(nextBricksData[i]);
        }
        return copy;
    }
}
