import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private MenuPanel menuPanel;
    private BoxWorldLevel1 panelLevel1;
    private BoxWorldLevel2 panelLevel2;
    private BoxWorldLevel3 panelLevel3;
    private BoxWorldLevel4 panelLevel4;
    private BoxWorldLevel5 panelLevel5;

    private int lastLevelCompleted = 0;

    public GameFrame() {
        this.setTitle("BoxWorld");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        menuPanel = new MenuPanel(this, lastLevelCompleted);
        showMenu();

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public int getLastLevelCompleted() {
        return lastLevelCompleted;
    }

    public void setLastLevelCompleted(int level) {
        if (level > lastLevelCompleted) {
            lastLevelCompleted = level;
        }
    }

    public void showLevel1() {
        if (panelLevel1 == null) {
            panelLevel1 = new BoxWorldLevel1(this);
        }
        reloadLevel1();
    }

    public void reloadLevel1() {
        if (panelLevel1 != null) {
            this.getContentPane().removeAll();
            panelLevel1.resetLevel();
            this.add(panelLevel1);
            this.pack();
            this.revalidate();
            this.repaint();
            panelLevel1.requestFocusInWindow();
        }
    }

    public void showLevel2() {
        if (panelLevel2 == null) {
            panelLevel2 = new BoxWorldLevel2(this);
        }
        reloadLevel2();
    }

    public void reloadLevel2() {
        if (panelLevel2 != null) {
            this.getContentPane().removeAll();
            panelLevel2.resetLevel();
            this.add(panelLevel2);
            this.pack();
            this.revalidate();
            this.repaint();
            panelLevel2.requestFocusInWindow();
        }
    }

    public void showLevel3() {
        if (panelLevel3 == null) {
            panelLevel3 = new BoxWorldLevel3(this);
        }
        reloadLevel3();
    }

    public void reloadLevel3() {
        if (panelLevel3 != null) {
            this.getContentPane().removeAll();
            panelLevel3.resetLevel();
            this.add(panelLevel3);
            this.pack();
            this.revalidate();
            this.repaint();
            panelLevel3.requestFocusInWindow();
        }
    }

    public void showLevel4() {
        if (panelLevel4 == null) {
            panelLevel4 = new BoxWorldLevel4(this);
        }
        reloadLevel4();
    }

    public void reloadLevel4() {
        if (panelLevel4 != null) {
            this.getContentPane().removeAll();
            panelLevel4.resetLevel();
            this.add(panelLevel4);
            this.pack();
            this.revalidate();
            this.repaint();
            panelLevel4.requestFocusInWindow();
        }
    }

    public void showLevel5() {
        if (panelLevel5 == null) {
            panelLevel5 = new BoxWorldLevel5(this);
        }
        reloadLevel5();
    }

    public void reloadLevel5() {
        if (panelLevel5 != null) {
            this.getContentPane().removeAll();
            panelLevel5.resetLevel();
            this.add(panelLevel5);
            this.pack();
            this.revalidate();
            this.repaint();
            panelLevel5.requestFocusInWindow();
        }
    }

    public void showMenu() {
        this.getContentPane().removeAll();
        menuPanel = new MenuPanel(this, lastLevelCompleted);  // update menu with current progress
        this.add(menuPanel);
        this.pack();
        this.revalidate();
        this.repaint();
        menuPanel.requestFocusInWindow();
    }

    /**
     * Called when a level is complete; update progress and show next level or menu.
     */
    public void notifyLevelComplete(int levelNumber) {
        setLastLevelCompleted(levelNumber);

        String message;
        switch (levelNumber) {
            case 1 -> message = "Level 1 complete! Proceeding to Level 2.";
            case 2 -> message = "Level 2 complete! Proceeding to Level 3.";
            case 3 -> message = "Level 3 complete! Proceeding to Level 4.";
            case 4 -> message = "Level 4 complete! Proceeding to Level 5.";
            case 5 -> message = "Level 5 complete! You have finished the game.";
            default -> message = "Level " + levelNumber + " complete!";
        }

        int option = JOptionPane.showConfirmDialog(this,
                message,
                "Level Complete",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            switch (levelNumber) {
                case 1 -> showLevel2();
                case 2 -> showLevel3();
                case 3 -> showLevel4();
                case 4 -> showLevel5();
                case 5 -> {
                    JOptionPane.showMessageDialog(this, "Congratulations! You completed all levels!");
                    showMenu();
                }
                default -> showMenu();
            }
        }
    }
}