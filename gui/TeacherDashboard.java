package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;

// Improved UI organization using concepts from Lab 11.
public class TeacherDashboard extends JFrame {
    public TeacherDashboard() {
        setTitle("Teacher Dashboard");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel titleLabel = new JLabel("Teacher Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // --- Main Content Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // --- Marks Management Panel ---
        JPanel marksPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        marksPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Marks Management", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        marksPanel.add(createStyledButton("Quiz Marks", "quiz"));
        marksPanel.add(createStyledButton("Assignment Marks", "assignment"));
        marksPanel.add(createStyledButton("Mid Term Marks", "mid"));
        marksPanel.add(createStyledButton("Final Marks", "final"));
        mainPanel.add(marksPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // --- Attendance Panel ---
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Attendance", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        JButton startAttendanceBtn = new JButton("Start Facial Recognition");
        startAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        startAttendanceBtn.addActionListener(e -> startAttendanceScript());
        attendancePanel.add(startAttendanceBtn, BorderLayout.CENTER);
        mainPanel.add(attendancePanel);

        add(mainPanel, BorderLayout.CENTER);

        // --- Logout Button ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createStyledButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.addActionListener(e -> new MarksEntryFrame(type));
        return button;
    }

    private void startAttendanceScript() {
        try {
            String scriptPath = new File("attendance/python/recognize_faces.py").getAbsolutePath();
            new ProcessBuilder("python", scriptPath).inheritIO().start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to start attendance script.\nEnsure Python is installed and in your system's PATH.",
                "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}