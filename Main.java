
// REMOVED: UIManager and SwingUtilities are no longer needed for the simpler launch.

public class Main {
    /**
     * MODIFIED: The main method is simplified to launch the GUI directly.
     * This matches the simpler examples in the GUI lecture.
     * The UIManager and invokeLater calls have been removed.
     */
    public static void main(String[] args) {
        // Directly create an instance of the LoginFrame.
        new LoginFrame();
    }
}