package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TeacherDashboard extends JFrame {

    private final String teacherUsername;
    private JComboBox<String> classSelector;

    public TeacherDashboard(String username) {
        this.teacherUsername = username;

        setTitle("Teacher Dashboard");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setupUI(); // Central method to build the UI
        setVisible(true);
    }

    /**
     * Initializes and lays out the main UI components of the dashboard.
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Add Header, Main Content, and Footer panels
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Teacher Dashboard: " + teacherUsername);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(titleLabel);
        return panel;
    }

    /**
     * Creates the central panel that holds the main dashboard controls.
     */
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        // Use BoxLayout to stack components vertically
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createClassSelectorPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createMarksPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createAttendancePanel());
        
        return mainPanel;
    }

    /**
     * Creates the panel for class selection.
     */
    private JPanel createClassSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Class"));
        
        // In a real app, this data would come from a file or database
        String[] taughtClasses = {"CY1121 - OOP Lab", "CY2311 - DLD Lab", "CY1123 - OOP"};
        classSelector = new JComboBox<>(taughtClasses);
        classSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(new JLabel("My Classes:"));
        panel.add(classSelector);
        return panel;
    }

    /**
     * Creates the panel for marks management actions.
     */
    private JPanel createMarksPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // Gap between title and buttons
        panel.setBorder(BorderFactory.createTitledBorder("Actions for Selected Class"));

        JLabel title = new JLabel("Marks Management", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);
        
        // Create a grid for the action buttons
        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonGrid.add(createDashboardButton("Quiz Marks", "quiz"));
        buttonGrid.add(createDashboardButton("Assignment Marks", "assignment"));
        buttonGrid.add(createDashboardButton("Mid Term Marks", "mid"));
        buttonGrid.add(createDashboardButton("Final Marks", "final"));
        
        panel.add(buttonGrid, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the panel for starting the attendance system.
     */
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Attendance"));
        JButton startAttendanceBtn = new JButton("Start Facial Recognition Attendance");
        startAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        startAttendanceBtn.addActionListener(e -> startAttendanceScript());
        panel.add(startAttendanceBtn, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * A helper method to create and configure a dashboard action button.
     * @param text The text displayed on the button.
     * @param type The type of action (e.g., "quiz", "assignment").
     */
    private JButton createDashboardButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.addActionListener(e -> {
            String classIdentifier = getSelectedClassIdentifier();
            new MarksEntryFrame(type, classIdentifier); // Opens the marks entry window
        });
        return button;
    }
    
    /**
     * Executes the Python facial recognition script.
     */
    private void startAttendanceScript() {
        String classIdentifier = getSelectedClassIdentifier();
        try {
            String scriptPath = new File("attendance/python/recognize_faces.py").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, "--section", classIdentifier);
            pb.inheritIO(); // Shows script output in the console
            pb.start();
            JOptionPane.showMessageDialog(this, "Starting attendance for " + classIdentifier, "Attendance Started", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to start attendance script.\nEnsure Python is installed and in your system's PATH.",
                "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedClassIdentifier() {
        String selected = (String) classSelector.getSelectedItem();
        // Return course code (e.g., "CY1121") or a default value
        return (selected != null) ? selected.split(" - ")[0] : "NONE";
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        panel.add(logoutButton);
        return panel;
    }
}