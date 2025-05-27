package gui;

import util.CSVManager;
import util.GradeCalculator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class StudentDashboard extends JFrame {
    public StudentDashboard(String username) {
        setTitle("Student Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top title label
        JLabel titleLabel = new JLabel("Your Academic Performance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Text area for marks
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(10, 10, 10, 10));

        try {
            List<String[]> data = CSVManager.loadMarks("data/marks.csv");
            boolean found = false;
            for (String[] row : data) {
                if (row[1].equals(username)) {
                    found = true;
                    int total = Integer.parseInt(row[2]) + Integer.parseInt(row[3]) +
                                Integer.parseInt(row[4]) + Integer.parseInt(row[5]);
                    String grade = GradeCalculator.calculateAbsolute(total);
                    area.append("Subject: " + row[0] + "\n");
                    area.append("Quiz: " + row[2] + ", Assignment: " + row[3] +
                                ", Mid: " + row[4] + ", Final: " + row[5] + "\n");
                    area.append("Total: " + total + " | Grade: " + grade + "\n\n");
                }
            }
            if (!found) {
                area.setText("No marks found for user: " + username);
            }
        } catch (IOException e) {
            e.printStackTrace();
            area.setText("Error loading marks.");
        }

        JScrollPane scrollPane = new JScrollPane(area);

        // Add everything to frame
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}
