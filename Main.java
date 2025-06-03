import gui.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Applying a more modern "Look and Feel" to the entire application.
        // This is a standard Swing feature to improve UI appearance.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // The GUI should be created on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}