import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BoxWorldLevel3 extends JPanel {
    static final int GRID_SIZE = 12;
    static final int UNIT_SIZE = 50;
    static final int PANEL_WIDTH = GRID_SIZE * UNIT_SIZE;
    static final int PANEL_HEIGHT = GRID_SIZE * UNIT_SIZE + 50;

    LevelCharacteristics caract;
    boolean levelComplete = false;
    boolean hasBananaPower = false;  // Banana power flag for pushing two boxes
    GameFrame parentFrame;

    private JButton restartButton;

    // Additional grid cell type for banana power-up
    private static final int CELL_BANANA = 5;

    /**
     * Constructor initializes the panel, restart button, key listener, and level state.
     */
    public BoxWorldLevel3(GameFrame parentFrame) {
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

        // Handle keyboard presses for player movement and box pushing
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

                if (nextCell == LevelCharacteristics.CELL_EMPTY || nextCell == LevelCharacteristics.CELL_EXIT || nextCell == CELL_BANANA) {
                    if (nextCell == CELL_BANANA) {
                        // Gain banana power on pickup
                        hasBananaPower = true;
                        caract.grid[newRow][newCol] = LevelCharacteristics.CELL_EMPTY;
                    }
                    if (nextCell == LevelCharacteristics.CELL_EXIT) {
                        levelComplete = true;
                        repaint();
                        SwingUtilities.invokeLater(() -> parentFrame.notifyLevelComplete(3));
                        return;
                    }
                    // Move player forward
                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                    caract.playerRow = newRow;
                    caract.playerCol = newCol;
                    caract.grid[newRow][newCol] = LevelCharacteristics.CELL_PLAYER;
                    repaint();
                } else if (nextCell == LevelCharacteristics.CELL_BOX) {
                    int box1Row = newRow;
                    int box1Col = newCol;
                    int box2Row = box1Row + dr;
                    int box2Col = box1Col + dc;
                    if (!caract.inBounds(box2Row, box2Col)) return;

                    int box2Cell = caract.grid[box2Row][box2Col];

                    if (hasBananaPower && box2Cell == LevelCharacteristics.CELL_BOX) {
                        int box3Row = box2Row + dr;
                        int box3Col = box2Col + dc;
                        if (!caract.inBounds(box3Row, box3Col)) return;
                        int box3Cell = caract.grid[box3Row][box3Col];

                        if (box3Cell == LevelCharacteristics.CELL_EMPTY) {
                            // Push two boxes forward
                            caract.grid[box3Row][box3Col] = LevelCharacteristics.CELL_BOX;
                            caract.grid[box2Row][box2Col] = LevelCharacteristics.CELL_BOX;
                            caract.grid[box1Row][box1Col] = LevelCharacteristics.CELL_PLAYER;
                            caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                            caract.playerRow = box1Row;
                            caract.playerCol = box1Col;
                            repaint();
                        }
                    } else {
                        // Push single box
                        if (box2Cell == LevelCharacteristics.CELL_EMPTY) {
                            caract.grid[box2Row][box2Col] = LevelCharacteristics.CELL_BOX;
                            caract.grid[box1Row][box1Col] = LevelCharacteristics.CELL_PLAYER;
                            caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                            caract.playerRow = box1Row;
                            caract.playerCol = box1Col;
                            repaint();
                        }
                    }
                }
            }
        });
    }

    /**
     * Initialize or reset the level board, player, boxes, exit, and banana power.
     */
    private void initLevel() {
        levelComplete = false;
        hasBananaPower = false;
        caract = new LevelCharacteristics(GRID_SIZE, GRID_SIZE / 2, 0, GRID_SIZE / 2, GRID_SIZE - 1);

        // Fill all cells with walls
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                caract.grid[r][c] = LevelCharacteristics.CELL_WALL;
            }
        }

        int midRow = GRID_SIZE / 2;

        // Clear the middle row for horizontal path
        for (int c = 0; c < GRID_SIZE; c++) {
            caract.grid[midRow][c] = LevelCharacteristics.CELL_EMPTY;
        }

        // Create vertical path for banana placement (3 cells up)
        int bananaRow = midRow - 3;
        int bananaCol = 1;
        for (int r = bananaRow; r <= midRow; r++) {
            caract.grid[r][bananaCol] = LevelCharacteristics.CELL_EMPTY;
        }

        // Place banana power-up cell
        caract.grid[bananaRow][bananaCol] = CELL_BANANA;

        // Place player start cell
        caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_PLAYER;

        // Define bypass area rectangle (4x3 cells)
        int bypassTopRow = midRow - 1;
        int bypassLeftCol = GRID_SIZE / 2 - 2;

        // Clear bypass area
        for (int r = bypassTopRow; r < bypassTopRow + 3; r++) {
            for (int c = bypassLeftCol; c < bypassLeftCol + 4; c++) {
                caract.grid[r][c] = LevelCharacteristics.CELL_EMPTY;
            }
        }

        // Place two boxes shifted by one row higher (bypass)
        int bypassRowForBoxes = bypassTopRow + 1; // one row up
        int box1Col = bypassLeftCol;
        int box2Col = bypassLeftCol + 1;
        caract.grid[bypassRowForBoxes][box1Col] = LevelCharacteristics.CELL_BOX;
        caract.grid[bypassRowForBoxes][box2Col] = LevelCharacteristics.CELL_BOX;

        // Place exit cell at rightmost middle row
        caract.grid[caract.exitRow][caract.exitCol] = LevelCharacteristics.CELL_EXIT;
    }

    /**
     * Reset the level by re-initializing and repaining.
     */
    public void resetLevel() {
        initLevel();
        repaint();
    }

    /**
     * Paint the level grid, player, boxes, banana, exit and the banana power indicator bubble.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Font originalFont = g.getFont();

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int x = c * UNIT_SIZE;
                int y = r * UNIT_SIZE;

                // Draw grid cell border
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
                    case CELL_BANANA -> {
                        // Draw banana cell as a small centered yellow bubble
                        int bubbleSize = 12;
                        int centerX = x + (UNIT_SIZE - bubbleSize) / 2;
                        int centerY = y + (UNIT_SIZE - bubbleSize) / 2;
                        g.setColor(Color.YELLOW);
                        g.fillOval(centerX, centerY, bubbleSize, bubbleSize);
                        g.setColor(Color.ORANGE.darker());
                        g.drawOval(centerX, centerY, bubbleSize, bubbleSize);
                    }
                    case LevelCharacteristics.CELL_PLAYER -> {
                        if (caract.playerImage != null) {
                            int iw = caract.playerImage.getWidth(this);
                            int ih = caract.playerImage.getHeight(this);
                            int offX = (UNIT_SIZE - iw) / 2;
                            int offY = (UNIT_SIZE - ih) / 2;
                            g.drawImage(caract.playerImage, x + offX, y + offY, this);
                        }
                        // Draw yellow power bubble at top-left corner if banana power active
                        if (hasBananaPower) {
                            int bubbleSize = 12;
                            int margin = 4;
                            g.setColor(Color.YELLOW);
                            g.fillOval(x + margin, y + margin, bubbleSize, bubbleSize);
                            g.setColor(Color.ORANGE.darker());
                            g.drawOval(x + margin, y + margin, bubbleSize, bubbleSize);
                        }
                    }
                    default -> {
                        // Empty cells just show grid border
                    }
                }
            }
        }
        g.setFont(originalFont);
    }
}