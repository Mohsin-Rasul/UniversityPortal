package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TeacherDashboard extends JFrame {
    public TeacherDashboard() {
        setTitle("Teacher Dashboard");
        setSize(450, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title label with custom font and color
        JLabel title = new JLabel("Choose Marks Type");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(45, 118, 232)); // nice blue
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(30, 10, 20, 10));

        // Styled buttons
        JButton quizBtn = createStyledButton("Enter Quiz Marks");
        JButton assignmentBtn = createStyledButton("Enter Assignment Marks");
        JButton midBtn = createStyledButton("Enter Mid Marks");
        JButton finalBtn = createStyledButton("Enter Final Marks");
        JButton startAttendanceBtn = createStyledButton("Start Attendance");

        // Action listeners
        quizBtn.addActionListener(e -> new MarksEntryFrame("quiz"));
        assignmentBtn.addActionListener(e -> new MarksEntryFrame("assignment"));
        midBtn.addActionListener(e -> new MarksEntryFrame("mid"));
        finalBtn.addActionListener(e -> new MarksEntryFrame("final"));

        startAttendanceBtn.addActionListener(e -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "attendance/python/recognize_faces.py");
                pb.inheritIO().start();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to start attendance script.");
            }
        });

        // Panel with GridLayout to hold all buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 30, 60));
        buttonPanel.setBackground(Color.WHITE);

        buttonPanel.add(quizBtn);
        buttonPanel.add(assignmentBtn);
        buttonPanel.add(midBtn);
        buttonPanel.add(finalBtn);
        buttonPanel.add(startAttendanceBtn); // newly added

        // Layout and theme
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(45, 118, 232));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(30, 90, 180));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(45, 118, 232));
            }
        });

        return button;
    }
}
