package gui;

import model.Mark;
import util.CSVManager;
import util.GradeCalculator;
import util.AttendanceManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class StudentDashboard extends JFrame {

    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        // Adjusted size to better accommodate the new attendance table format
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Your Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Marks", createMarksPanel(username));
        tabbedPane.addTab("My Attendance", createAttendancePanel(username));
        add(tabbedPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        southPanel.add(logoutButton);
        add(southPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createMarksPanel(String username) {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Subject", "Quiz", "Assignment", "Mid", "Final", "Total", "Grade"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        try {
            List<Mark> marks = CSVManager.loadMarks("data/marks.csv");
            for (Mark mark : marks) {
                if (mark.getUsername().equals(username)) {
                    int total = mark.getQuiz() + mark.getAssignment() + mark.getMid() + mark.getFinalExam();
                    String grade = GradeCalculator.calculateAbsolute(total);
                    tableModel.addRow(new Object[]{
                        mark.getSubject(), mark.getQuiz(), mark.getAssignment(),
                        mark.getMid(), mark.getFinalExam(), total, grade
                    });
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading marks data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel(String username) {
        JPanel panel = new JPanel(new BorderLayout());
        // Updated column names as per the image
        String[] columnNames = {"Sr. no", "Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22); // Adjusted for better look with more rows
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Adjusted font for table
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // Sr. no
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Date
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Status


        List<String[]> records = AttendanceManager.getAttendanceRecords();
        int serialNumber = 1;
        if (records != null) {
            for (String[] row : records) {
                // row[0] is Username, row[1] is Timestamp (YYYY-MM-DD HH:MM:SS), row[2] is Section
                if (row.length >= 2 && row[0].trim().equalsIgnoreCase(username.trim())) {
                    String fullTimestamp = row[1];
                    String date = fullTimestamp.split(" ")[0]; // Extract only the date part
                    // For now, status is always "Present" as the system only logs presence.
                    tableModel.addRow(new Object[]{serialNumber++, date, "Present"});
                }
            }
        }
        if (serialNumber == 1) { // No records found for this student
            // Optionally, display a message or leave the table empty.
            // For now, the table will be empty if no records.
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}