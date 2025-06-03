package gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TeacherDashboard extends JFrame {

    private JComboBox<String> sectionSelector;
    private JLabel marksManagementTitle;

    public TeacherDashboard() {
        setTitle("Teacher Dashboard");
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        updateDynamicTitles();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Teacher Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createSectionSelectorPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        mainPanel.add(createMarksPanel());
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(createAttendancePanel());
        
        return mainPanel;
    }

    private JPanel createSectionSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Section"));
        String[] sections = {"Section A (BCY243001 - BCY243050)", "Section B (BCY243051 - BCY243100)"};
        sectionSelector = new JComboBox<>(sections);
        sectionSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionSelector.addActionListener(e -> updateDynamicTitles());
        panel.add(new JLabel("Current Section:"));
        panel.add(sectionSelector);
        return panel;
    }
    
    private void updateDynamicTitles() {
        String selectedSectionDescription = (String) sectionSelector.getSelectedItem();
        if (selectedSectionDescription != null && marksManagementTitle != null) {
            String sectionName = getSelectedSectionIdentifier();
            marksManagementTitle.setText("Marks Management - Section " + sectionName);
        }
    }

    private String getSelectedSectionIdentifier() {
        String selectedSectionDescription = (String) sectionSelector.getSelectedItem();
        if (selectedSectionDescription != null) {
            if (selectedSectionDescription.contains("Section B")) {
                return "B";
            }
        }
        return "A"; // Default to A
    }

    private JPanel createMarksPanel() {
        JPanel marksOuterPanel = new JPanel(new BorderLayout());
        marksManagementTitle = new JLabel("Marks Management", SwingConstants.LEFT);
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
        titledMarksPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        titledMarksPanel.add(marksOuterPanel, BorderLayout.CENTER);

        return titledMarksPanel;
    }

    private JPanel createAttendancePanel() {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder("Attendance"));
        // Updated button text
        JButton startAttendanceBtn = new JButton("Start Attendance for Selected Section");
        startAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        startAttendanceBtn.addActionListener(e -> startAttendanceScript());
        attendancePanel.add(startAttendanceBtn, BorderLayout.CENTER);
        return attendancePanel;
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

    private JButton createStyledButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.addActionListener(e -> {
            String sectionIdentifier = getSelectedSectionIdentifier();
            new MarksEntryFrame(type, sectionIdentifier);
        });
        return button;
    }
    
    private void startAttendanceScript() {
        String sectionIdentifier = getSelectedSectionIdentifier();
        try {
            String scriptPath = new File("attendance/python/recognize_faces.py").getAbsolutePath();
            // Pass the section identifier as a command-line argument
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, "--section", sectionIdentifier);
            pb.inheritIO();
            pb.start();
            JOptionPane.showMessageDialog(this, "Starting attendance for Section " + sectionIdentifier + ".\nPress 'q' in the recognition window to quit.", "Attendance Started", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to start attendance script.\nEnsure Python is installed and in your system's PATH.",
                "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}