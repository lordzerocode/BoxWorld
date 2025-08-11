import javax.swing.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // Print the current working directory to console
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        // Set a default handler for uncaught exceptions in any thread
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logException(throwable, thread.getName());
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, throwable.toString(),
                        "Uncaught Exception in Thread " + thread.getName(), JOptionPane.ERROR_MESSAGE);
            });
        });

        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    GameFrame frame = new GameFrame();
                    // Optionally set frame visible if not done inside GameFrame constructor
                    // frame.setVisible(true);
                } catch (Throwable t) {
                    logException(t, "EDT");
                    showErrorDialog(t);
                }
            });
        } catch (Throwable t) {
            logException(t, "main");
            showErrorDialog(t);
        }
    }

    private static void showErrorDialog(Throwable t) {
        t.printStackTrace(); // print stack trace in console (visible if running from CMD)
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    t.toString(),
                    "Error starting game",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    private static void logException(Throwable t, String threadName) {
        try (FileWriter fw = new FileWriter("error_log.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println("=== Exception in thread: " + threadName + " ===");
            t.printStackTrace(pw);
            pw.println("=======================================");
        } catch (IOException ex) {
            // If logging fails, silently ignore
        }
    }
}