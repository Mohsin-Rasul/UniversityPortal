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

// GUI upgraded to use JTable, a standard component covered conceptually in Lab 11 (GUI Components).
public class StudentDashboard extends JFrame {

    public StudentDashboard(String username) {
        setTitle("Student Dashboard - " + username);
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel titleLabel = new JLabel("Your Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // --- Tabbed Pane for Marks and Attendance ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Marks", createMarksPanel(username));
        tabbedPane.addTab("My Attendance", createAttendancePanel(username));
        add(tabbedPane, BorderLayout.CENTER);

        // --- Logout Button ---
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
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
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
                    Object[] rowData = {
                        mark.getSubject(),
                        mark.getQuiz(),
                        mark.getAssignment(),
                        mark.getMid(),
                        mark.getFinalExam(),
                        total,
                        grade
                    };
                    tableModel.addRow(rowData);
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
        String[] columnNames = {"Date & Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        List<String[]> records = AttendanceManager.getAttendanceRecords();
        if (records != null) {
            for (String[] row : records) {
                if (row.length >= 2 && row[0].trim().equalsIgnoreCase(username.trim())) {
                    tableModel.addRow(new Object[]{row[1]});
                }
            }
        }
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}