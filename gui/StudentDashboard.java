package gui;

import util.CSVManager;
import util.GradeCalculator;
import util.AttendanceManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class StudentDashboard extends JFrame {

    public StudentDashboard(String username) {
        setTitle("Student Dashboard");
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title Label
        JLabel titleLabel = new JLabel("Your Academic Performance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Marks Text Area
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
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

        // View Attendance Button
        JButton viewAttendanceBtn = new JButton("View Attendance");
        viewAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    viewAttendanceBtn.addActionListener(e -> {
    try {
        List<String[]> records = AttendanceManager.getAttendanceRecords();
        System.out.println("Attendance Records:");
        for (String[] row : records) {
            System.out.println("Row: " + String.join(",", row));
        }

        StringBuilder sb = new StringBuilder("Your Attendance:\n\n");
        boolean found = false;
        for (String[] row : records) {
            if (row[0].trim().equals(username.trim())) {
                found = true;
                sb.append("Date: ").append(row[1]).append("\n");
            }
        }
        if (!found) {
            sb.append("No attendance record found for user: ").append(username);
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Attendance", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading attendance.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});



        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(viewAttendanceBtn);

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
