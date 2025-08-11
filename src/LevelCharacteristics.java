import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.IOException;

public class LevelCharacteristics {
    public int[][] grid;
    public int playerRow, playerCol;
    public int exitRow, exitCol;
    public BufferedImage playerImage;

    // Cell type constants
    public static final int CELL_EMPTY = 0;
    public static final int CELL_PLAYER = 1;
    public static final int CELL_WALL = 2;
    public static final int CELL_EXIT = 3;
    public static final int CELL_BOX = 4;
    public static final int CELL_BANANA = 5;

    // Number of bananas collected by player (0 to 2)
    private int bananaCount = 0;

    /**
     * Constructor initializes grid and player/exit positions,
     * loads and resizes player image resource.
     */
    public LevelCharacteristics(int gridSize, int startPlayerRow, int startPlayerCol, int exitRow, int exitCol) {
        grid = new int[gridSize][gridSize];
        this.playerRow = startPlayerRow;
        this.playerCol = startPlayerCol;
        this.exitRow = exitRow;
        this.exitCol = exitCol;

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("/stickman.png"));
            int newSize = (int)(60 * 0.8);
            playerImage = resizeImage(originalImage, newSize, newSize);
        } catch (IOException | IllegalArgumentException ex) {
            ex.printStackTrace();
            playerImage = null;
        }
    }

    /**
     * Resize image to specified dimensions.
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
    }

    /**
     * Returns true if (r,c) is within grid bounds.
     */
    public boolean inBounds(int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[0].length;
    }

    /**
     * Gets current banana count.
     */
    public int getBananaCount() {
        return bananaCount;
    }

    /**
     * Increments banana count by 1 up to max 2.
     */
    public void addBanana() {
        if (bananaCount < 2) {
            bananaCount++;
        }
    }

    /**
     * Resets banana count to zero.
     */
    public void resetBananas() {
        bananaCount = 0;
    }
}