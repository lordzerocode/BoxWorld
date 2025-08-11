import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    public MenuPanel(GameFrame frame, int lastLevelCompleted) {
        setPreferredSize(new Dimension(600, 600));
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        JButton startButton = new JButton("Start Level 1");
        startButton.setFont(new Font("Arial", Font.BOLD, 24));
        startButton.addActionListener(e -> frame.showLevel1());

        JLabel titleLabel = new JLabel("BoxWorld");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(titleLabel, gbc);

        gbc.gridy = 1;
        add(startButton, gbc);

        // Show Level 5 button only if player finished all levels (lastLevelCompleted >= 5)
        if (lastLevelCompleted >= 5) {
            JButton level5Button = new JButton("Start Level 5");
            level5Button.setFont(new Font("Arial", Font.PLAIN, 18));
            level5Button.addActionListener(e -> frame.showLevel5());

            gbc.gridy = 2;
            add(level5Button, gbc);
        }
    }
}