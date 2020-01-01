import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    // PROPERTIES
    public static final int intMove = 25; // # of pixels moved every time
    public static final int intBlockSize = 25; // Block size (25*25px)
    public static int intXMax = intBlockSize * 10; // 10 blocks wide
    public static int intYMax = intBlockSize * 20; // 20 blocks tall
    public static int[][] intGrid = new int[intYMax / intBlockSize][intXMax / intBlockSize]; // 10x20 array grid of board
    public static Block blockCurrent;
    public static Block blockGhost;
    public static Block blockHeld;

    public static int intBag = -1; // -1 is initial value to generate the 1st & 2nd pieceArrays
    public static int intRandom = -1;
    public static Integer[] pieceArray = new Integer[]{1, 2, 3, 4, 5, 6, 7};
    public static Integer[] pieceArrayNext = new Integer[]{1, 2, 3, 4, 5, 6, 7};

    // METHODS
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g; // Use Graphics2D instead of regular Graphics
        super.paintComponent(g2); // Clear previous drawings (Windows only); super JPanel (original) paintComponent method

        g2.setColor(Color.DARK_GRAY); // Background
        g2.fillRect(0, 0, intXMax, intYMax);

        drawOldBlocks(g2); // Draw previously stored blocks
        drawGridlines(0, 0, intXMax, intYMax, intXMax / intBlockSize, intYMax / intBlockSize, g2); // Draw board gridlines

        drawGhostBlock(g2); // Draw ghost block (so that current block can overlap when they intersect)
        drawCurrentBlock(g2); // Draw current block on board

        drawHeldBlock(g2); // Draw held block on sidebar
        drawNextBlocks(g2); // Draw next blocks on side bar

        if (Controller.checkCollision(blockCurrent, "down") == true) { // Block hits bottom
            storeOldBlocks(blockCurrent);
            removeFullLines(intGrid);
            if (blockCurrent.intY <= 0 && blockCurrent.intX == BoardPanel.intBlockSize * 3) { // Collision at block spawn point
                Tetris.blnGameLoop = false; // end game
            } else { // If no collision at spawn point, generate a new block
                blockCurrent = Controller.generateBlock();
                Controller.updateGhostBlock(blockCurrent); // Update position of ghost block
            }
        }
    }

    private void drawBlock(Block blockDraw, int intX, int intY, Graphics2D g2) { // Draw block
        g2.setColor(blockDraw.colBlock);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (blockDraw.intCurrentCoords[i][j] != 0) {
                    g2.fillRect(intX + (j * intBlockSize), intY + (i * intBlockSize), intBlockSize, intBlockSize); // Draw block

                    // Draw block outline
                    g2.setColor(Color.BLACK);
                    g2.drawRect(intX + (j * intBlockSize), intY + (i * intBlockSize), intBlockSize, intBlockSize);
                    g2.setColor(blockDraw.colBlock);
                }
            }
        }
    }

    private void drawGhostBlock(Graphics2D g2) {
        g2.setColor(blockGhost.colBlock);
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (blockGhost.intCurrentCoords[i][j] != 0) {
                    g2.drawRect(blockGhost.intX + 1 + (j * intBlockSize), blockGhost.intY + 1 + (i * intBlockSize), intBlockSize - 2, intBlockSize - 2);
                }
            }
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void drawCurrentBlock(Graphics2D g2) { // Draw current block on board
        drawBlock(blockCurrent, blockCurrent.intX, blockCurrent.intY, g2);
    }

    private void drawHeldBlock(Graphics2D g2) { // Draw held block on sidebar
        int intHeldX = intXMax + 20;
        int intHeldY = 25;

        if (blockHeld != null) { // Run if there is a held block
            drawBlock(blockHeld, intHeldX, intHeldY, g2); // Draws held block
        }

        g2.setColor(Color.BLACK);
        g2.drawString("HOLD", intHeldX, intHeldY - 8);
        drawGridlines(intHeldX, intHeldY, intHeldX + (intBlockSize * 4), intHeldY + (intBlockSize * 4), 4, 4, g2);
    }

    private void drawNextBlocks(Graphics2D g2) { // Draw next 3 blocks on sidebar
        g2.setStroke(new BasicStroke(1)); // Set thin outline stroke

        int intNextX = intXMax + 20;
        int intNextY = 160;
        Block blockNext1;
        Block blockNext2;
        Block blockNext3;

        if (intBag > 6) { // If value of intBag is greater than 6, read from next pieceArray
            blockNext1 = new Block(pieceArrayNext[intBag - 7]);
        } else {
            blockNext1 = new Block(pieceArray[intBag]);
        }
        if (intBag + 1 > 6) { // If value of intBag+1 is greater than 6, read from next pieceArray
            blockNext2 = new Block(pieceArrayNext[intBag - 6]);
        } else {
            blockNext2 = new Block(pieceArray[intBag + 1]);
        }
        if (intBag + 2 > 6) { // If value of intBag+2 is greater than 6, read from next pieceArray
            blockNext3 = new Block(pieceArrayNext[intBag - 5]);
        } else {
            blockNext3 = new Block(pieceArray[intBag + 2]);
        }

        drawBlock(blockNext1, intNextX, intNextY, g2);
        drawBlock(blockNext2, intNextX, intNextY + (intBlockSize * 4) + 0, g2);
        drawBlock(blockNext3, intNextX, intNextY + (2 * ((intBlockSize * 4) + 00)), g2);

        g2.setColor(Color.BLACK);
        g2.drawString("NEXT", intNextX, intNextY - 8);
        //drawGridlines(intNextX, intNextY, intNextX + (intBlockSize*4), intNextY + (intBlockSize*12), 4, 12, g2);
        drawGridlines(intNextX, intNextY, intNextX + (intBlockSize * 4), intNextY + (intBlockSize * 4), 4, 4, g2);
        drawGridlines(intNextX, intNextY + (intBlockSize * 4), intNextX + (intBlockSize * 4), intNextY + (intBlockSize * 8), 4, 4, g2);
        drawGridlines(intNextX, intNextY + (intBlockSize * 8), intNextX + (intBlockSize * 4), intNextY + (intBlockSize * 12), 4, 4, g2);
    }

    private void storeOldBlocks(Block blockCurrent) {
        for (int a = 0; a < 4; a++) { // Loop through entire block coordsArray (all 16 values of the 4x4 array)
            for (int b = 0; b < 4; b++) {
                if (blockCurrent.intCurrentCoords[a][b] != 0) { // If block coordsArray is not empty
                    intGrid[(blockCurrent.intY / intBlockSize) + a][(blockCurrent.intX / intBlockSize) + b] = blockCurrent.intType; // Fill intGrid array w/ corresponding integer type values
                } // If nothing in block coordsArray, intGrid[(blockCurrent.intY / intBlockSize) + a][(blockCurrent.intX / intBlockSize) + b] remains at 0
            }
        }
    }

    private void drawOldBlocks(Graphics2D g2) {
        for (int c = 0; c < (intYMax / intBlockSize); c++) {
            for (int d = 0; d < (intXMax / intBlockSize); d++) {
                if (intGrid[c][d] != 0) {
                    switch (intGrid[c][d]) { // Set block colours of corresponding block/values in intGrid
                        case Block.IBlock:
                            //g2.setColor(new Color(0, 240, 240)); // Cyan
                            g2.setColor(new Color(0, 200, 240)); // Darker cyan
                            break;
                        case Block.LBlock:
                            //g2.setColor(new Color(240, 160, 0)); // Orange
                            g2.setColor(new Color(240, 130, 0)); // Darker orange
                            break;
                        case Block.JBlock:
                            //g2.setColor(new Color(0, 0, 240)); // Darker blue
                            g2.setColor(new Color(0, 80, 255)); // Lighter blue
                            break;
                        case Block.SBlock:
                            //g2.setColor(new Color(0, 240, 0)); // Green
                            g2.setColor(new Color(0, 200, 0)); // Darker green
                            break;
                        case Block.ZBlock:
                            g2.setColor(new Color(240, 0, 0)); // Red
                            break;
                        case Block.TBlock:
                            g2.setColor(new Color(160, 0, 240)); // Purple
                            break;
                        case Block.OBlock:
                            //g2.setColor(new Color(240, 240, 0)); // Yellow
                            g2.setColor(new Color(240, 200, 0)); // Yellow
                            break;
                    }
                    g2.fillRect(d * intBlockSize, c * intBlockSize, intBlockSize, intBlockSize); // Draw oldBlocks
                }
            }
        }
    }

    private void removeFullLines(int[][] intGrid) {
        for (int y = 0; y < (intYMax / intBlockSize); y++) {
            if (intGrid[y][0] != 0 && intGrid[y][1] != 0 && intGrid[y][2] != 0 && intGrid[y][3] != 0 && intGrid[y][4] != 0 && intGrid[y][5] != 0 && intGrid[y][6] != 0 && intGrid[y][7] != 0 && intGrid[y][8] != 0 && intGrid[y][9] != 0) { // Full Row
                for (int a = 0; a < y; a++) {
                    //intGrid[y - a] = intGrid[y - a - 1];  // Shift all blocks above down 1 block (DOESN'T WORK, SINCE intGrid[y-a] SIMPLY BECOMES A REFERENCE FOR intGrid[y-a-1]
                    System.arraycopy(intGrid[y - a - 1], 0, intGrid[y - a], 0, 10); // Shift all blocks above down 1 block, by copying the array
                }
            }
        }
    }

    private void drawGridlines(int intX1, int intY1, int intX2, int intY2, int intAmountVert, int intAmountHoriz, Graphics2D g2) { // Draw gridlines on board. intAmountX & Y determine how many gridlines to draw
        g2.setColor(Color.BLACK);
        for (int a = 0; a <= intAmountVert; a++) { // Draw thick outline outside vertical grid
            if (a == 0 || a == intAmountVert) {
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawLine(intX1 + (a * intBlockSize), intY1, intX1 + (a * intBlockSize), intY2); // Vertical gridlines
            //g2.drawLine(a*intBlockSize, 0, a*intBlockSize, intYMax); // Vertical gridlines (board)
        }
        for (int b = 0; b <= intAmountHoriz; b++) {
            if (b == 0 || b == intAmountHoriz) { // Draw thick outline outside horizontal grid
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setStroke(new BasicStroke(1));
            }
            g2.drawLine(intX1, intY1 + (b * intBlockSize), intX2, intY1 + (b * intBlockSize)); // Horizontal gridlines
            //g2.drawLine(0, b*intBlockSize, intXMax, b*intBlockSize); // Horizontal gridlines (board)
        }
    }

    // CONSTRUCTOR
    public BoardPanel() {
        super();
        blockCurrent = Controller.generateBlock(); // Generate a new block
    }
}
