package com.comp2042.model;

/**
 * Represents the result of a row-clear operation.
 * Stores how many lines were removed, the updated board matrix,
 * and the score bonus awarded.
 */
public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    /**
     * Creates a new ClearRow result.
     *
     * @param linesRemoved number of lines cleared
     * @param newMatrix    resulting board matrix after clearing
     * @param scoreBonus   score awarded for this clear
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /** @return number of lines cleared */
    public int getLinesRemoved() {
        return linesRemoved;
    }
    /** @return a defensive copy of the updated board matrix */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /** @return score bonus for this clear */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
