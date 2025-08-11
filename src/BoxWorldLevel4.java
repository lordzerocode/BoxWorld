import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Level 4 of BoxWorld on 15x15 grid.
 * Player starts at (7,0).
 * Map cells unchanged from previous layout,
 * plus added free path in specified cells and horizontal path to exit.
 */
public class BoxWorldLevel4 extends JPanel {
    static final int GRID_SIZE = 15;
    static final int UNIT_SIZE = 50;
    static final int PANEL_WIDTH = GRID_SIZE * UNIT_SIZE;
    static final int PANEL_HEIGHT = GRID_SIZE * UNIT_SIZE + 50;

    LevelCharacteristics caract;
    boolean levelComplete = false;
    GameFrame parentFrame;

    private JButton restartButton;

    private static final int BUBBLE_SIZE = 12;
    private static final int BUBBLE_MARGIN = 4;

    public BoxWorldLevel4(GameFrame parentFrame) {
        this.parentFrame = parentFrame;

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        restartButton = new JButton("Restart Level");
        restartButton.setBounds(10, GRID_SIZE * UNIT_SIZE + 10, 150, 30);
        add(restartButton);

        restartButton.addActionListener(e -> {
            resetLevel();
            requestFocusInWindow();
        });

        initLevel();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (levelComplete) return;

                int dr = 0, dc = 0;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> dc = -1;
                    case KeyEvent.VK_RIGHT -> dc = 1;
                    case KeyEvent.VK_UP -> dr = -1;
                    case KeyEvent.VK_DOWN -> dr = 1;
                    default -> { return; }
                }

                int newRow = caract.playerRow + dr;
                int newCol = caract.playerCol + dc;
                if (!caract.inBounds(newRow, newCol)) return;

                int nextCell = caract.grid[newRow][newCol];

                // Moving into empty, exit or banana cell
                if (nextCell == LevelCharacteristics.CELL_EMPTY
                        || nextCell == LevelCharacteristics.CELL_EXIT
                        || nextCell == LevelCharacteristics.CELL_BANANA) {

                    if (nextCell == LevelCharacteristics.CELL_BANANA) {
                        caract.addBanana();
                        caract.grid[newRow][newCol] = LevelCharacteristics.CELL_EMPTY;
                    }

                    if (nextCell == LevelCharacteristics.CELL_EXIT) {
                        levelComplete = true;
                        repaint();
                        SwingUtilities.invokeLater(() -> parentFrame.notifyLevelComplete(4));
                        return;
                    }

                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                    caract.playerRow = newRow;
                    caract.playerCol = newCol;
                    caract.grid[newRow][newCol] = LevelCharacteristics.CELL_PLAYER;
                    repaint();

                    return;
                }

                // Box pushing logic: push only if contiguous boxes followed by empty
                if (nextCell == LevelCharacteristics.CELL_BOX) {
                    int maxBoxes = caract.getBananaCount() + 1;

                    int boxCount = 0;
                    int checkR = caract.playerRow + dr;
                    int checkC = caract.playerCol + dc;

                    while (boxCount < maxBoxes
                            && caract.inBounds(checkR, checkC)
                            && caract.grid[checkR][checkC] == LevelCharacteristics.CELL_BOX) {
                        boxCount++;
                        checkR += dr;
                        checkC += dc;
                    }

                    if (!caract.inBounds(checkR, checkC)) return;
                    if (caract.grid[checkR][checkC] != LevelCharacteristics.CELL_EMPTY) return;
                    if (boxCount == 0) return;

                    for (int i = boxCount - 1; i >= 0; i--) {
                        int fromR = caract.playerRow + dr * (i + 1);
                        int fromC = caract.playerCol + dc * (i + 1);
                        int toR = fromR + dr;
                        int toC = fromC + dc;
                        caract.grid[toR][toC] = LevelCharacteristics.CELL_BOX;
                        caract.grid[fromR][fromC] = LevelCharacteristics.CELL_EMPTY;
                    }

                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                    caract.playerRow += dr;
                    caract.playerCol += dc;
                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_PLAYER;
                    repaint();
                }
            }
        });
    }

    /**
     * Initialize/reset the level layout, respecting previous layout,
     * plus additional free path clarifications.
     */
    private void initLevel() {
        levelComplete = false;
        caract = new LevelCharacteristics(GRID_SIZE, 7, 0, 7, 14);
        caract.resetBananas();

        // Fill all with walls initially
        for (int r = 0; r < GRID_SIZE; r++)
            for (int c = 0; c < GRID_SIZE; c++)
                caract.grid[r][c] = LevelCharacteristics.CELL_WALL;

        // Access path from (2,0) to (6,0)
        for (int r = 2; r <= 6; r++)
            caract.grid[r][0] = LevelCharacteristics.CELL_EMPTY;

        // Access path cell (2,1)
        caract.grid[2][1] = LevelCharacteristics.CELL_EMPTY;

        // Bypass 1: 3x3 at (1,2)
        for (int r = 1; r <= 3; r++)
            for (int c = 2; c <= 4; c++)
                caract.grid[r][c] = LevelCharacteristics.CELL_EMPTY;

        // One box at (2,2)
        caract.grid[2][2] = LevelCharacteristics.CELL_BOX;

        // Bypass 2: 4x3 at (10,2)
        for (int r = 10; r <= 12; r++)
            for (int c = 2; c <= 5; c++)
                caract.grid[r][c] = LevelCharacteristics.CELL_EMPTY;

        // Boxes at (11,2), (11,3)
        caract.grid[11][2] = LevelCharacteristics.CELL_BOX;
        caract.grid[11][3] = LevelCharacteristics.CELL_BOX;

        // Bypass 3: 5x3 at (6,4)
        for (int r = 6; r <= 10; r++)
            for (int c = 4; c <= 6; c++)
                caract.grid[r][c] = LevelCharacteristics.CELL_EMPTY;

        // Three boxes at (7,4), (7,5), (7,6)
        caract.grid[7][4] = LevelCharacteristics.CELL_BOX;
        caract.grid[7][5] = LevelCharacteristics.CELL_BOX;
        caract.grid[7][6] = LevelCharacteristics.CELL_BOX;

        // Walls at (9,4), (9,5), (9,6)
        caract.grid[9][4] = LevelCharacteristics.CELL_WALL;
        caract.grid[9][5] = LevelCharacteristics.CELL_WALL;
        caract.grid[9][6] = LevelCharacteristics.CELL_WALL;

        // Wall at (10,6) (from previous)
        caract.grid[10][6] = LevelCharacteristics.CELL_WALL;

        // Free path at these specified cells:
        int[][] freeCells = {
                {6,7}, {6,8},
                {7,1}, {7,2}, {7,3}, {7,7}, {7,8}, {7,9},
                {8,7}, {8,8}
        };
        for (int[] cell : freeCells) {
            caract.grid[cell[0]][cell[1]] = LevelCharacteristics.CELL_EMPTY;
        }

        // Open horizontal path from (7,9) up to exit (7,14)
        for (int c = 9; c <= 14; c++) {
            caract.grid[7][c] = LevelCharacteristics.CELL_EMPTY;
        }

        // Player start (7,0)
        caract.grid[7][0] = LevelCharacteristics.CELL_PLAYER;

        // Exit (7,14)
        caract.grid[7][14] = LevelCharacteristics.CELL_EXIT;

        // Downward access path from (7,0) to (11,0)
        for (int r = 8; r <= 11; r++) {
            caract.grid[r][0] = LevelCharacteristics.CELL_EMPTY;
        }

        // Connect horizontally (11,0) to (11,1)
        caract.grid[11][1] = LevelCharacteristics.CELL_EMPTY;

        // Bananas for powerups
        caract.grid[1][4] = LevelCharacteristics.CELL_BANANA;
        caract.grid[12][5] = LevelCharacteristics.CELL_BANANA;
        // No banana at (9,6), replaced by wall previously
    }

    public void resetLevel() {
        initLevel();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font originalFont = g.getFont();

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int x = c * UNIT_SIZE;
                int y = r * UNIT_SIZE;

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x, y, UNIT_SIZE, UNIT_SIZE);

                int cell = caract.grid[r][c];
                switch (cell) {
                    case LevelCharacteristics.CELL_WALL -> {
                        g.setColor(Color.DARK_GRAY.darker());
                        g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
                    }
                    case LevelCharacteristics.CELL_EXIT -> {
                        g.setColor(Color.YELLOW);
                        g.fillRect(x + 5, y + 5, UNIT_SIZE - 10, UNIT_SIZE - 10);
                    }
                    case LevelCharacteristics.CELL_BOX -> {
                        g.setColor(new Color(139, 69, 19));
                        g.fillRect(x + 10, y + 10, UNIT_SIZE - 20, UNIT_SIZE - 20);
                        g.setColor(Color.BLACK);
                        g.drawRect(x + 10, y + 10, UNIT_SIZE - 20, UNIT_SIZE - 20);
                    }
                    case LevelCharacteristics.CELL_BANANA -> {
                        int bubbleSize = 12;
                        int centerX= x + (UNIT_SIZE - bubbleSize)/2;
                        int centerY= y + (UNIT_SIZE - bubbleSize)/2;
                        g.setColor(Color.YELLOW);
                        g.fillOval(centerX, centerY, bubbleSize, bubbleSize);
                        g.setColor(Color.ORANGE.darker());
                        g.drawOval(centerX, centerY, bubbleSize, bubbleSize);
                    }
                    case LevelCharacteristics.CELL_PLAYER -> {
                        if (caract.playerImage != null) {
                            int iw = caract.playerImage.getWidth(this);
                            int ih = caract.playerImage.getHeight(this);
                            int offX = (UNIT_SIZE - iw)/2;
                            int offY = (UNIT_SIZE - ih)/2;
                            g.drawImage(caract.playerImage, x + offX, y + offY, this);
                        }
                        int bananaCount = caract.getBananaCount();
                        if (bananaCount > 0) {
                            g.setColor(Color.YELLOW);
                            g.fillOval(x + BUBBLE_MARGIN, y + BUBBLE_MARGIN, BUBBLE_SIZE, BUBBLE_SIZE);
                            g.setColor(Color.ORANGE.darker());
                            g.drawOval(x + BUBBLE_MARGIN, y + BUBBLE_MARGIN, BUBBLE_SIZE, BUBBLE_SIZE);
                            if (bananaCount > 1) {
                                int secondX = x + UNIT_SIZE - BUBBLE_MARGIN - BUBBLE_SIZE;
                                g.setColor(Color.YELLOW);
                                g.fillOval(secondX, y + BUBBLE_MARGIN, BUBBLE_SIZE, BUBBLE_SIZE);
                                g.setColor(Color.ORANGE.darker());
                                g.drawOval(secondX, y + BUBBLE_MARGIN, BUBBLE_SIZE, BUBBLE_SIZE);
                            }
                        }
                    }
                    default -> {
                        // Empty cell grid lines only
                    }
                }
            }
        }

        g.setFont(originalFont);
    }
}