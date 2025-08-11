import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class BoxWorldLevel5 extends JPanel {
    static final int GRID_SIZE = 20;
    static final int UNIT_SIZE = 34; // Cell size for a 20x20 grid
    static final int PANEL_SIZE = GRID_SIZE * UNIT_SIZE;

    LevelCharacteristics caract;
    boolean levelComplete = false;
    GameFrame parentFrame;
    private JButton restartButton;

    // Extra cell type for bananas
    private static final int CELL_BANANA = 5;

    public BoxWorldLevel5(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE + 50));
        setLayout(null);
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        restartButton = new JButton("Restart Level");
        restartButton.setBounds(10, PANEL_SIZE + 10, 150, 30);
        add(restartButton);

        restartButton.addActionListener(e -> {
            resetLevel();
            requestFocusInWindow();
        });

        initLevel();

        // After caract initialized, rescale player image for level 5 to fit inside unit cell
        if (caract.playerImage != null) {
            int newSize = (int) (UNIT_SIZE * 0.8); // 80% of cell size
            caract.playerImage = resizeImage(caract.playerImage, newSize, newSize);
        }

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

                if (nextCell == LevelCharacteristics.CELL_EMPTY ||
                        nextCell == LevelCharacteristics.CELL_EXIT ||
                        nextCell == CELL_BANANA) {

                    if (nextCell == CELL_BANANA) {
                        caract.addBanana();
                        caract.grid[newRow][newCol] = LevelCharacteristics.CELL_EMPTY;
                    }

                    if (nextCell == LevelCharacteristics.CELL_EXIT) {
                        levelComplete = true;
                        repaint();
                        SwingUtilities.invokeLater(() -> parentFrame.notifyLevelComplete(5));
                        return;
                    }

                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                    caract.playerRow = newRow;
                    caract.playerCol = newCol;
                    caract.grid[newRow][newCol] = LevelCharacteristics.CELL_PLAYER;
                    repaint();

                } else if (nextCell == LevelCharacteristics.CELL_BOX) {
                    int maxPush = 1 + caract.getBananaCount(); // number of boxes player can push depending on bananas collected
                    int boxesInLine = 0;
                    for (int step = 0; step < maxPush; step++) {
                        int checkRow = caract.playerRow + (dr * (step + 1));
                        int checkCol = caract.playerCol + (dc * (step + 1));
                        if (!caract.inBounds(checkRow, checkCol)) break;
                        if (caract.grid[checkRow][checkCol] == LevelCharacteristics.CELL_BOX) {
                            boxesInLine++;
                        } else if (caract.grid[checkRow][checkCol] == LevelCharacteristics.CELL_EMPTY) {
                            break;
                        } else {
                            break;
                        }
                    }
                    if (boxesInLine > 0 && boxesInLine <= maxPush) {
                        int destRow = caract.playerRow + dr * (boxesInLine + 1);
                        int destCol = caract.playerCol + dc * (boxesInLine + 1);
                        if (caract.inBounds(destRow, destCol) &&
                                caract.grid[destRow][destCol] == LevelCharacteristics.CELL_EMPTY) {
                            for (int n = boxesInLine; n >= 1; n--) {
                                int fromRow = caract.playerRow + dr * n;
                                int fromCol = caract.playerCol + dc * n;
                                int toRow = caract.playerRow + dr * (n + 1);
                                int toCol = caract.playerCol + dc * (n + 1);
                                caract.grid[toRow][toCol] = LevelCharacteristics.CELL_BOX;
                                caract.grid[fromRow][fromCol] = LevelCharacteristics.CELL_EMPTY;
                            }
                            caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                            caract.playerRow += dr;
                            caract.playerCol += dc;
                            caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_PLAYER;
                            repaint();
                        }
                    }
                }
            }
        });
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    private void initLevel() {
        levelComplete = false;
        caract = new LevelCharacteristics(GRID_SIZE, 9, 0, 16, 19); // player at (9,0), exit at (16,19)
        caract.resetBananas();

        // Initialize all cells as walls
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                caract.grid[r][c] = LevelCharacteristics.CELL_WALL;
            }
        }

        // Free path cells including additions and removals as requested
        int[][] freePaths = {
                {0, 7}, {0, 17},
                {1, 4}, {1, 5}, {1, 6}, {1, 7}, {1, 8}, {1, 9}, {1, 10}, {1, 11},
                {1, 12}, {1, 13}, {1, 14}, {1, 15}, {1, 16}, {1, 17}, {1, 19},
                {4, 1}, // new free path
                {5, 0}, {5, 1}, {5, 3}, {5, 4}, {5, 5}, {6, 3}, {6, 4}, {6, 5},
                {4, 4}, {3, 4}, {2, 6}, {2, 7}, {2, 8}, {3, 16}, {4, 16}, {4, 17},
                {5, 17}, {5, 16}, {5, 15}, {5, 14}, {5, 13}, {4, 13}, {4, 14},
                {6, 13}, {7, 13}, {6, 11}, {7, 11}, {8, 11}, {4, 10}, {5, 10},
                {6, 10}, {7, 10}, {7, 9}, {5, 9}, {7, 1}, {8, 1}, {9, 1},
                {13, 1}, {14, 1}, {15, 1}, {16, 1}, {12, 5}, {14, 4}, {14, 5},
                {13, 5}, {13, 6}, {13, 7}, {14, 7}, {14, 8}, {14, 9}, {13, 9},
                {12, 9}, /* 11,9 removed here to be wall */
                {13, 10}, {11, 11}, {11, 12}, {11, 13}, {13, 19},
                {11, 14}, {12, 11}, {13, 11}, {13, 12}, {13, 13}, {14, 11},
                {15, 11}, {15, 12}, {15, 13}, {14, 15}, // 14,15 is free now
                /* 14,18 removed here, stays wall */
                {13, 17}, {13, 18},
                {14, 16}, {15, 16}, {16, 16}, {17, 16}, {18, 16},
                {19, 16}, {19, 18}, {15, 17}, {16, 18}
        };

        for (int[] cell : freePaths) {
            caract.grid[cell[0]][cell[1]] = LevelCharacteristics.CELL_EMPTY;
        }

        // Set player start
        caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_PLAYER;

        // Set exit point
        caract.grid[caract.exitRow][caract.exitCol] = LevelCharacteristics.CELL_EXIT;

        // Place boxes except 14,14 which is now a wall
        int[][] boxCoords = {
                {1, 7}, {1, 8}, {1, 18}, {2, 16}, {2, 4}, {2, 5}, {5, 2}, {5, 11},
                {6, 1}, {6, 9}, {6, 12}, {10, 1}, {11, 1}, {12, 1}, {13, 2}, {13, 3},
                {13, 4}, {13, 8}, {12, 12}, {12, 13}, {12, 14}, {13, 14}, {14, 12},
                {14, 13}, /* 14, 14 removed for wall */ {14, 15}, {13, 15}, {13, 16}, {14, 18},
                {16, 17}, {17, 17}, {18, 17}, {19, 17}, {13, 13} // box at 13,13 remains
        };

        for (int[] pos : boxCoords) {
            caract.grid[pos[0]][pos[1]] = LevelCharacteristics.CELL_BOX;
        }

        // Set walls replacing former box at 14,14 and other requested walls
        caract.grid[14][14] = LevelCharacteristics.CELL_WALL;
        caract.grid[11][9] = LevelCharacteristics.CELL_WALL;
        caract.grid[14][18] = LevelCharacteristics.CELL_WALL;

        // Place bananas as before
        caract.grid[0][18] = CELL_BANANA;
        caract.grid[4][11] = CELL_BANANA;
    }

    public void resetLevel() {
        initLevel();

        // Rescale player image again after reset
        if (caract.playerImage != null) {
            int newSize = (int) (UNIT_SIZE * 0.8);
            caract.playerImage = resizeImage(caract.playerImage, newSize, newSize);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

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
                        g.fillRect(x + 5, y + 5, UNIT_SIZE - 10, UNIT_SIZE - 10);
                        g.setColor(Color.BLACK);
                        g.drawRect(x + 5, y + 5, UNIT_SIZE - 10, UNIT_SIZE - 10);
                    }
                    case CELL_BANANA -> {
                        int bSize = UNIT_SIZE / 2;
                        int bOff = (UNIT_SIZE - bSize) / 2;
                        g.setColor(Color.YELLOW);
                        g.fillOval(x + bOff, y + bOff, bSize, bSize);
                        g.setColor(Color.ORANGE.darker());
                        g.drawOval(x + bOff, y + bOff, bSize, bSize);
                    }
                    case LevelCharacteristics.CELL_PLAYER -> {
                        if (caract.playerImage != null) {
                            int iw = caract.playerImage.getWidth(this);
                            int ih = caract.playerImage.getHeight(this);
                            int offX = (UNIT_SIZE - iw) / 2;
                            int offY = (UNIT_SIZE - ih) / 2;
                            g.drawImage(caract.playerImage, x + offX, y + offY, this);
                        } else {
                            drawStickman(g, x, y);
                        }
                        int bananaCount = caract.getBananaCount();
                        if (bananaCount > 0) {
                            int bSize = 10, margin = 4;
                            for (int i = 0; i < bananaCount; i++) {
                                g.setColor(i == 0 ? Color.YELLOW : Color.ORANGE);
                                g.fillOval(x + margin + (bSize + 2) * i, y + margin, bSize, bSize);
                            }
                        }
                    }
                }
            }
        }
    }

    // Draw a smaller stickman that fits neatly within the cell
    private void drawStickman(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int cell = UNIT_SIZE;
        int centerX = x + cell / 2;
        int topY = y + cell / 6; // slightly lower to center better
        int headRadius = cell / 9; // smaller head radius for better fit

        g2.setColor(Color.BLUE);
        g2.fillOval(centerX - headRadius, topY, 2 * headRadius, 2 * headRadius);

        g2.setColor(Color.BLACK);
        int bodyTop = topY + 2 * headRadius;
        int bodyBottom = y + cell * 4 / 5;
        g2.drawLine(centerX, bodyTop, centerX, bodyBottom);

        int armY = bodyTop + cell / 12;
        int armLen = cell / 5;
        g2.drawLine(centerX - armLen, armY, centerX + armLen, armY);

        int legLen = cell / 5;
        g2.drawLine(centerX, bodyBottom, centerX - legLen, bodyBottom + legLen);
        g2.drawLine(centerX, bodyBottom, centerX + legLen, bodyBottom + legLen);

        g2.dispose();
    }
}