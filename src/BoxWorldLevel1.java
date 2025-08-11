import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoxWorldLevel1 extends JPanel {
    static final int GRID_SIZE = 10;
    static final int UNIT_SIZE = 60;
    static final int PANEL_SIZE = GRID_SIZE * UNIT_SIZE;

    LevelCharacteristics caract;
    boolean levelComplete = false;
    GameFrame parentFrame;

    private JButton restartButton;

    public BoxWorldLevel1(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE + 50)); // spațiu pentru buton jos
        setLayout(null); // layout absolut pentru plasare buton
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        // Buton Restart
        restartButton = new JButton("Restart Level");
        restartButton.setBounds(10, PANEL_SIZE + 10, 150, 30);
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
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> dc = -1;
                    case KeyEvent.VK_RIGHT -> dc = 1;
                    case KeyEvent.VK_UP -> dr = -1;
                    case KeyEvent.VK_DOWN -> dr = 1;
                }

                int newRow = caract.playerRow + dr;
                int newCol = caract.playerCol + dc;

                if (!caract.inBounds(newRow, newCol)) return;

                int nextCell = caract.grid[newRow][newCol];

                if (nextCell == LevelCharacteristics.CELL_EMPTY || nextCell == LevelCharacteristics.CELL_EXIT) {
                    if (nextCell == LevelCharacteristics.CELL_EXIT) {
                        levelComplete = true;
                        repaint();
                        SwingUtilities.invokeLater(() -> parentFrame.notifyLevelComplete(1));
                        return;
                    }

                    caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                    caract.playerRow = newRow;
                    caract.playerCol = newCol;
                    caract.grid[newRow][newCol] = LevelCharacteristics.CELL_PLAYER;
                    repaint();

                } else if (nextCell == LevelCharacteristics.CELL_BOX) {
                    int boxNewRow = newRow + dr;
                    int boxNewCol = newCol + dc;

                    if (!caract.inBounds(boxNewRow, boxNewCol)) return;

                    int boxNextCell = caract.grid[boxNewRow][boxNewCol];

                    if (boxNextCell == LevelCharacteristics.CELL_EMPTY) {
                        caract.grid[boxNewRow][boxNewCol] = LevelCharacteristics.CELL_BOX;
                        caract.grid[newRow][newCol] = LevelCharacteristics.CELL_PLAYER;
                        caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_EMPTY;
                        caract.playerRow = newRow;
                        caract.playerCol = newCol;
                        repaint();
                    }
                }
            }
        });
    }

    private void initLevel() {
        levelComplete = false;
        caract = new LevelCharacteristics(GRID_SIZE, GRID_SIZE / 2, 1, GRID_SIZE / 2, GRID_SIZE - 1);

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                caract.grid[r][c] = LevelCharacteristics.CELL_WALL;
            }
        }

        int midRow = GRID_SIZE / 2;
        for (int c = 0; c < GRID_SIZE; c++) {
            caract.grid[midRow][c] = LevelCharacteristics.CELL_EMPTY;
        }

        caract.grid[caract.playerRow][caract.playerCol] = LevelCharacteristics.CELL_PLAYER;

        int boxCol = 5;
        caract.grid[midRow][boxCol] = LevelCharacteristics.CELL_BOX;

        caract.grid[caract.exitRow][caract.exitCol] = LevelCharacteristics.CELL_EXIT;

        caract.grid[midRow][6] = LevelCharacteristics.CELL_EMPTY;
        caract.grid[midRow][7] = LevelCharacteristics.CELL_EMPTY;
        caract.grid[midRow][8] = LevelCharacteristics.CELL_EMPTY;

        for (int c = 6; c <= 8; c++) {
            caract.grid[midRow - 1][c] = LevelCharacteristics.CELL_EMPTY;
            caract.grid[midRow + 1][c] = LevelCharacteristics.CELL_EMPTY;
        }
    }

    // Call to reset level (restart)
    public void resetLevel() {
        initLevel();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r=0; r<GRID_SIZE; r++) {
            for (int c=0;c<GRID_SIZE;c++) {
                int x = c * UNIT_SIZE;
                int y = r * UNIT_SIZE;

                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(x,y,UNIT_SIZE,UNIT_SIZE);

                int cell = caract.grid[r][c];
                switch(cell) {
                    case LevelCharacteristics.CELL_WALL -> {
                        g.setColor(Color.DARK_GRAY.darker());
                        g.fillRect(x,y,UNIT_SIZE,UNIT_SIZE);
                    }
                    case LevelCharacteristics.CELL_EXIT -> {
                        g.setColor(Color.YELLOW);
                        g.fillRect(x+5,y+5,UNIT_SIZE-10,UNIT_SIZE-10);
                    }
                    case LevelCharacteristics.CELL_BOX -> {
                        g.setColor(new Color(139,69,19));
                        g.fillRect(x+10,y+10,UNIT_SIZE-20,UNIT_SIZE-20);
                        g.setColor(Color.BLACK);
                        g.drawRect(x+10,y+10,UNIT_SIZE-20,UNIT_SIZE-20);
                    }
                    case LevelCharacteristics.CELL_PLAYER -> {
                        if (caract.playerImage != null) {
                            int iw = caract.playerImage.getWidth(this);
                            int ih = caract.playerImage.getHeight(this);
                            int offX = (UNIT_SIZE - iw)/2;
                            int offY = (UNIT_SIZE - ih)/2;
                            g.drawImage(caract.playerImage, x+offX, y+offY,this);
                        } else {
                            drawStickman(g,x,y);
                        }
                    }
                }
            }
        }
    }

    private void drawStickman(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int cell = UNIT_SIZE;
        int centerX = x + cell / 2;
        int topY = y + cell / 8;
        int headRadius = cell / 6;

        g2.setColor(Color.BLUE);
        g2.fillOval(centerX - headRadius, topY, 2 * headRadius, 2 * headRadius);

        g2.setColor(Color.BLACK);
        int bodyTop = topY + 2 * headRadius;
        int bodyBottom = y + cell * 4 / 5;
        g2.drawLine(centerX, bodyTop, centerX, bodyBottom);

        int armY = bodyTop + cell / 8;
        int armLen = cell / 3;
        g2.drawLine(centerX - armLen, armY, centerX + armLen, armY);

        int legLen = cell / 3;
        g2.drawLine(centerX, bodyBottom, centerX - legLen, bodyBottom + legLen);
        g2.drawLine(centerX, bodyBottom, centerX + legLen, bodyBottom + legLen);

        g2.dispose();
    }
}