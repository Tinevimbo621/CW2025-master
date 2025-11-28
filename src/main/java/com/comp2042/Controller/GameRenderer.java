package com.comp2042.Controller;


import com.comp2042.model.ViewData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;


public class GameRenderer {
        private static final int BRICK_SIZE = 20;
        private static final int BOARD_START_ROW = 2;
        private static final double BRICK_PANEL_Y_OFFSET = -42;
        private static final double GHOST_OPACITY = 0.3;
        private static final int BRICK_CORNER_ARC = 9;
        private static final double NEXT_BRICK_CELL_SIZE = BRICK_SIZE;

        private final GridPane gamePanel;
        private final GridPane brickPanel;
        private final VBox nextBricksPanel;
        private final GridPane holdPanel;
    private GameController gameController;

        private Rectangle[][] displayMatrix;
        private Rectangle[][] ghostMatrix;
        private Rectangle[][] rectangles;

        public GameRenderer(GridPane gamePanel, GridPane brickPanel,
                            VBox nextBricksPanel, GridPane holdPanel, Paint[] colorMap) {
            this.gamePanel = gamePanel;
            this.brickPanel = brickPanel;
            this.nextBricksPanel = nextBricksPanel;
            this.holdPanel = holdPanel;
        }


        /**
         * Gets the fill color for a given color index.
         *
         * @param colorIndex The color index
         * @return The corresponding Paint color
         */
        Paint getFillColor(int colorIndex) {
            if (colorIndex >= 0 && colorIndex < GuiController.COLOR_MAP.length) {
                return GuiController.COLOR_MAP[colorIndex];
            }
            return GuiController.COLOR_MAP[GuiController.COLOR_MAP.length - 1]; // Return default color
        }

        /**
         * Creates a standard brick rectangle.
         *
         * @return Rectangle with default brick properties
         */
        Rectangle createBrickRectangle() {
            Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
            rectangle.setFill(Color.TRANSPARENT);
            return rectangle;
        }

        /**
         * Creates a ghost rectangle with reduced opacity.
         *
         * @return Rectangle with ghost properties
         */
        Rectangle createGhostRectangle() {
            Rectangle ghostRect = new Rectangle(BRICK_SIZE,BRICK_SIZE);
            ghostRect.setFill(Color.TRANSPARENT);
            ghostRect.setOpacity(GHOST_OPACITY);
            return ghostRect;
        }

        /**
         * Creates a rectangle with the specified color.
         *
         * @param colorValue    The color index
         * @return Rectangle with the specified color
         */
        Rectangle createColoredRectangle(int colorValue) {
            Rectangle rectangle = new Rectangle(BRICK_SIZE,BRICK_SIZE);
            rectangle.setFill(getFillColor(colorValue));
            return rectangle;
        }

        /**
         * Creates a 2D array of rectangles.
         *
         * @param rows Number of rows
         * @param cols Number of columns
         * @return 2D array of rectangles
         */
        Rectangle[][] createMatrix(int rows, int cols) {
            return new Rectangle[rows][cols];
        }

        /**
         * Initializes the display matrix for the game board.
         *
         * @param boardMatrix The board matrix
         */
        void initializeDisplayMatrix(int[][] boardMatrix) {
            displayMatrix = createMatrix(boardMatrix.length, boardMatrix[0].length);
            populateDisplayMatrix(boardMatrix);
        }

        /**
         * Populates the display matrix with rectangles and adds them to the game panel.
         *
         * @param boardMatrix   The board matrix
         */
        private void populateDisplayMatrix(int[][] boardMatrix) {
            for (int i = BOARD_START_ROW; i < boardMatrix.length; i++) {
                for (int j = 0; j < boardMatrix[i].length; j++) {
                    Rectangle rectangle = createBrickRectangle();
                   displayMatrix[i][j] = rectangle;
                   gamePanel.add(rectangle, j, i - BOARD_START_ROW);
                }
            }
        }

        /**
         * Initializes the ghost matrix for showing ghost pieces.
         *
         * @param boardMatrix   The board matrix
         */
        void initializeGhostMatrix(int[][] boardMatrix ) {
            ghostMatrix = createMatrix(boardMatrix.length, boardMatrix[0].length);
            populateGhostMatrix(boardMatrix);
        }

        /**
         * Populates the ghost matrix with semi-transparent rectangles.
         *
         * @param boardMatrix   The board matrix
         */
        private void populateGhostMatrix(int[][] boardMatrix) {
            for (int i = BOARD_START_ROW; i < boardMatrix.length; i++) {
                for (int j = 0; j < boardMatrix[i].length; j++) {
                    Rectangle ghostRect = createGhostRectangle();
                    ghostMatrix[i][j] = ghostRect;
                    gamePanel.add(ghostRect, j, i - BOARD_START_ROW);
                }
            }
        }

        /**
         * Initializes the brick panel with the initial brick.
         *
         * @param brick         The brick data
         */
        void initializeBrickPanel(ViewData brick) {
            rectangles = createMatrix(brick.getBrickData().length, brick.getBrickData()[0].length);
            populateBrickPanel(brick);
            positionBrickPanel(brick);
        }

        /**
         * Populates the brick panel with brick rectangles.
         *
         * @param brick         The brick data
         */
        private void populateBrickPanel(ViewData brick) {
           forEachCell(brick.getBrickData(), (row, col, value) -> {
                Rectangle rectangle = createColoredRectangle(value);
               rectangles[row][col] = rectangle;
               brickPanel.add(rectangle, col, row);
            });
        }

        /**
         * Positions the brick panel based on the brick's position.
         *
         * @param brick         The brick data
         */
        private void positionBrickPanel(ViewData brick) {
            double x = calculateBrickPanelX(brick);
            double y = calculateBrickPanelY(brick);

            brickPanel.setLayoutX(x);
            brickPanel.setLayoutY(y);
        }

        /**
         * Calculates the X position for the brick panel.
         *
         * @param brick         The brick data
         * @return X position
         */
        double calculateBrickPanelX(ViewData brick) {
            return gamePanel.getLayoutX() +
                    brick.getxPosition() * brickPanel.getVgap() +
                    brick.getxPosition() * BRICK_SIZE;
        }

        /**
         * Calculates the Y position for the brick panel.
         *
         * @param brick         The brick data
         * @return Y position
         */
        double calculateBrickPanelY(ViewData brick) {
            return BRICK_PANEL_Y_OFFSET + gamePanel.getLayoutY() +
                    brick.getyPosition() * brickPanel.getHgap() +
                    brick.getyPosition() * BRICK_SIZE;
        }


    /**
     * Refreshes the brick display with new position data.
     * Updates brick position, colors, and ghost piece
     */
  public void refreshBrick(ViewData brick, int ghostDistance) {
        updateBrickPosition(brick);
        updateBrickAppearance(brick);
      updateGhostBrick(brick, ghostDistance);
    }

        /**
         * Updates the position of the brick panel.
         *
         * @param brick         The brick data
         */
        private void updateBrickPosition(ViewData brick) {
            double x = calculateBrickPanelX(brick);
            double y = calculateBrickPanelY(brick);

            brickPanel.setLayoutX(x);
           brickPanel.setLayoutY(y);
        }

        /**
         * Updates the appearance of the brick rectangles.
         *
         * @param brick         The brick data
         */
        private void updateBrickAppearance(ViewData brick) {
            forEachCell(brick.getBrickData(), (row, col, value) ->
                    updateRectangleAppearance(value, rectangles[row][col]));
        }

        /**
         * Updates the appearance of a single rectangle.
         *
         * @param colorValue    The color value
         * @param rectangle     The rectangle to update
         */
        void updateRectangleAppearance(int colorValue, Rectangle rectangle) {
            rectangle.setFill(getFillColor(colorValue));
            rectangle.setArcHeight(BRICK_CORNER_ARC);
            rectangle.setArcWidth(BRICK_CORNER_ARC);
        }

        /**
         * Updates the ghost brick position and appearance.
         *
         * @param brick         The current brick data
         * @param ghostDistance
         */
        private void updateGhostBrick(ViewData brick, int ghostDistance) {

            drawGhost(brick, ghostDistance);
        }
    /**
         * Draws the ghost brick at the specified drop position.
         *
         * @param brick         The current brick data
         * @param ghostDistance The distance for the ghost to drop
         */
        private void drawGhost(ViewData brick, int ghostDistance) {
           clearGhostMatrix();
            drawGhostBrick(brick, ghostDistance);
        }

        /**
         * Draws the ghost brick at the calculated position
         *
         * @param brick         The brick data
         * @param ghostDistance the ghost drop distance
         */
        private void drawGhostBrick(ViewData brick, int ghostDistance) {
            if (ghostMatrix == null) return;

            int[][] data = brick.getBrickData();
            int x = brick.getxPosition();
            int y = brick.getyPosition() + ghostDistance;

            for (int row = 0; row < data.length; row++) {
                for (int col = 0; col < data[row].length; col++) {

                    if (data[row][col] == 0) continue;

                    int r = y + row;
                    int c = x + col;

                    // Bounds check – CRITICAL
                    if (r < 0 || r >= ghostMatrix.length) continue;
                    if (c < 0 || c >= ghostMatrix[r].length) continue;

                    // Null check – prevents all NPEs
                    if (ghostMatrix[r][c] != null) {
                       ghostMatrix[r][c].setFill(Color.LIGHTGRAY);
                    }
                }
            }
        }

        /**
         * Clears the ghost matrix.
         *
         */
        private void clearGhostMatrix() {
            for (int i = BOARD_START_ROW; i < ghostMatrix.length; i++) {
                for (int j = 0; j < ghostMatrix[i].length; j++) {
                    ghostMatrix[i][j].setFill(Color.TRANSPARENT);
                }
            }
        }

        /**
         * Updates the next shape preview panel.
         *
         * @param nextShapesMatrix The matrix for the next shapes
         */
        public void updateNextShapesPreview(int[][][] nextShapesMatrix) {
            nextBricksPanel.getChildren().clear();

            for (int[][] shapeMatrix : nextShapesMatrix) {
                GridPane panel = new GridPane();
                panel.setHgap(2);
                panel.setVgap(2);

                for (int r = 0; r < shapeMatrix.length; r++) {
                    for (int c = 0; c < shapeMatrix[r].length; c++) {
                        if (shapeMatrix[r][c] != 0) {
                            Rectangle block = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                            block.setFill(getFillColor(shapeMatrix[r][c]));
                            panel.add(block, c, r);
                        }
                    }
                }

                nextBricksPanel.getChildren().add(panel);
            }
        }

        /**
         * Renders the held brick in the hold panel.
         *
         * @param heldMatrix 2D array representing the held brick; non-zero values are drawn as colored blocks.
         */
        public void updateHeldBrick(int[][] heldMatrix) {
            if (holdPanel == null) return;
            holdPanel.getChildren().clear();

            if (heldMatrix == null) return;

            GridPane panel = new GridPane();
            panel.setHgap(2);
            panel.setVgap(2);

            for (int r = 0; r < heldMatrix.length; r++) {
                for (int c = 0; c < heldMatrix[r].length; c++) {
                    if (heldMatrix[r][c] != 0) {
                        Rectangle block = new Rectangle(NEXT_BRICK_CELL_SIZE,NEXT_BRICK_CELL_SIZE);
                        block.setFill(getFillColor(heldMatrix[r][c]));
                        panel.add(block, c, r);
                    }
                }
            }
           holdPanel.getChildren().add(panel);
        }

        /**
         * Refreshes the game background with updated board data.
         *
         * @param board The updated board matrix
         */
        public void refreshGameBackground(int[][] board) {
            for (int i =BOARD_START_ROW; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    updateRectangleAppearance(board[i][j],displayMatrix[i][j]);
                }
            }
        }

        /**
         * Executes an action for each cell in a matrix
         *
         * @param shape  the matrix to a process
         * @param action the action is to apply to each cell
         */
        private void forEachCell(int[][] shape, GuiController.CellAction action) {
            for (int j = 0; j < shape.length; j++) {
                for (int c = 0; c < shape[j].length; c++) {
                    action.apply(j, c, shape[j][c]);
                }
            }
        }
    }