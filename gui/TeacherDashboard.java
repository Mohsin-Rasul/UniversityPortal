package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TeacherDashboard extends JFrame {

    private JComboBox<String> classSelector;
    private String teacherUsername;

    public TeacherDashboard(String username) {
        this.teacherUsername = username;
        setTitle("Teacher Dashboard");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Teacher Dashboard: " + teacherUsername);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createClassSelectorPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createMarksPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createAttendancePanel());
        
        return mainPanel;
    }

    private JPanel createClassSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Class"));
        
        // This is sample data. In a real application, you would load this
        // from a file based on the logged-in teacher's username.
        String[] taughtClasses = {"CY1121 - OOP Lab", "CY2311 - DLD Lab", "CY1123 - OOP"};
        classSelector = new JComboBox<>(taughtClasses);
        classSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(new JLabel("My Classes:"));
        panel.add(classSelector);
        return panel;
    }

    private String getSelectedClassIdentifier() {
        String selected = (String) classSelector.getSelectedItem();
        if (selected != null) {
            return selected.split(" - ")[0]; // Returns the course code, e.g., "CY1121"
        }
        return "NONE";
    }

    private JPanel createMarksPanel() {
        JPanel marksOuterPanel = new JPanel(new BorderLayout());
        JLabel marksManagementTitle = new JLabel("Marks Management", SwingConstants.LEFT);
        marksManagementTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        marksManagementTitle.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        
        JPanel marksButtonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        marksButtonsPanel.add(createStyledButton("Quiz Marks", "quiz"));
        marksButtonsPanel.add(createStyledButton("Assignment Marks", "assignment"));
        marksButtonsPanel.add(createStyledButton("Mid Term Marks", "mid"));
        marksButtonsPanel.add(createStyledButton("Final Marks", "final"));
        
        marksOuterPanel.add(marksManagementTitle, BorderLayout.NORTH);
        marksOuterPanel.add(marksButtonsPanel, BorderLayout.CENTER);
        
        JPanel titledMarksPanel = new JPanel(new BorderLayout());
        titledMarksPanel.setBorder(BorderFactory.createTitledBorder("Actions for Selected Class"));
        titledMarksPanel.add(marksOuterPanel, BorderLayout.CENTER);

        return titledMarksPanel;
    }

    private JPanel createAttendancePanel() {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder("Attendance"));
        JButton startAttendanceBtn = new JButton("Start Facial Recognition Attendance");
        startAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        startAttendanceBtn.addActionListener(e -> startAttendanceScript());
        attendancePanel.add(startAttendanceBtn, BorderLayout.CENTER);
        return attendancePanel;
    }
    
    /**
     * **FIX APPLIED HERE**: This method now creates and opens the MarksEntryFrame
     * instead of showing a placeholder message.
     */
    private JButton createStyledButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.addActionListener(e -> {
            String classIdentifier = getSelectedClassIdentifier();
            // This line now correctly opens the marks entry window.
            new MarksEntryFrame(type, classIdentifier);
        });
        return button;
    }
    
    private void startAttendanceScript() {
        String classIdentifier = getSelectedClassIdentifier();
        try {
            String scriptPath = new File("attendance/python/recognize_faces.py").getAbsolutePath();
            // The python script is called with a "--section" argument, which now holds the class identifier
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, "--section", classIdentifier);
            pb.inheritIO();
            pb.start();
            JOptionPane.showMessageDialog(this, "Starting attendance for " + classIdentifier, "Attendance Started", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to start attendance script.\nEnsure Python is installed and in your system's PATH.",
                "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createFooterPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        southPanel.add(logoutButton);
        return southPanel;
    }
}