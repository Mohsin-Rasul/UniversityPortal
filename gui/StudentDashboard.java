package gui;

import model.Mark;
import util.CSVManager;
import util.ConfigManager;
import util.GradeCalculator;
import util.AttendanceManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class StudentDashboard extends JFrame {

    private final String username;
    private List<Mark> allMarks;
    private final JPanel detailPanel;
    private String gradingPolicy;

    // Define weights for grading
    private static final double QUIZ_WEIGHT = 0.20; // 20%
    private static final double ASSIGNMENT_WEIGHT = 0.20; // 20%
    private static final double MID_WEIGHT = 0.25; // 25%
    private static final double FINAL_WEIGHT = 0.35; // 35%

    public StudentDashboard(String username) {
        this.username = username;
        this.allMarks = new ArrayList<>();
        
        setTitle("Student Dashboard - " + username);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        loadAllMarks();
        this.gradingPolicy = ConfigManager.loadGradingPolicy();

        detailPanel = createDetailPanel();
        JScrollPane subjectListPanel = createSubjectListPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subjectListPanel, detailPanel);
        splitPane.setDividerLocation(250);

        add(createHeaderPanel("Your Academic Overview"), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadAllMarks() {
        try {
            this.allMarks = CSVManager.loadMarks("data/marks.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load marks data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private JScrollPane createSubjectListPanel() {
        DefaultListModel<String> subjectListModel = new DefaultListModel<>();
        String[] allAvailableSubjects = {"CY1121 - OOP Lab", "CY2311 - DLD Lab", "CY1123 - OOP"};
        for (String subject : allAvailableSubjects) {
            subjectListModel.addElement(subject);
        }

        JList<String> subjectList = new JList<>(subjectListModel);
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subjectList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        subjectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                updateDetailPanelContent(subjectList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(subjectList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Subjects"));
        return scrollPane;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
        JLabel placeholder = new JLabel("Select a subject from the left to see details.", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

    private void updateDetailPanelContent(String selectedSubjectName) {
        detailPanel.removeAll();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        String subjectCode = selectedSubjectName.split(" - ")[0].trim();
        
        Mark studentMark = null;
        for (Mark mark : allMarks) {
            if (mark.getSubject().equalsIgnoreCase(subjectCode) && mark.getUsername().equals(this.username)) {
                studentMark = mark;
                break;
            }
        }

        List<Double> allWeightedScores = new ArrayList<>();
        if ("relative".equals(gradingPolicy)) {
             for (Mark mark : allMarks) {
                if (mark.getSubject().equalsIgnoreCase(subjectCode)) {
                    allWeightedScores.add(calculateWeightedScore(mark));
                }
            }
        }

        detailPanel.add(createMarksDisplayPanel(studentMark, selectedSubjectName, allWeightedScores));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailPanel.add(createAttendanceDisplayPanel());

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private double calculateWeightedScore(Mark mark) {
        if (mark == null) return 0;
        
        double quizScore = (double) mark.getTotalQuizScore() / 40.0;
        double assignmentScore = (double) mark.getTotalAssignmentScore() / 40.0;
        double midScore = (double) mark.getMid() / 25.0;
        double finalScore = (double) mark.getFinalExam() / 35.0;

        return (quizScore * QUIZ_WEIGHT + assignmentScore * ASSIGNMENT_WEIGHT + midScore * MID_WEIGHT + finalScore * FINAL_WEIGHT) * 100;
    }

    private JPanel createMarksDisplayPanel(Mark mark, String subjectName, List<Double> allWeightedScores) {
        JPanel marksPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        marksPanel.setBorder(BorderFactory.createTitledBorder("Marks for " + subjectName));

        if (mark != null) {
            double weightedScore = calculateWeightedScore(mark);
            
            String finalGrade;
            String gradePolicyLabel;

            if ("relative".equals(this.gradingPolicy)) {
                finalGrade = GradeCalculator.calculateRelative(weightedScore, allWeightedScores);
                gradePolicyLabel = "Final Grade (Relative):";
            } else {
                finalGrade = GradeCalculator.calculateAbsolute(weightedScore);
                gradePolicyLabel = "Final Grade (Absolute):";
            }

            for (int i = 0; i < 4; i++) {
                addInfoRow(marksPanel, "Quiz " + (i + 1) + ":", String.valueOf(mark.getQuizzes()[i]), false);
            }
            marksPanel.add(new JSeparator()); marksPanel.add(new JSeparator());
            for (int i = 0; i < 4; i++) {
                addInfoRow(marksPanel, "Assignment " + (i + 1) + ":", String.valueOf(mark.getAssignments()[i]), false);
            }
            marksPanel.add(new JSeparator()); marksPanel.add(new JSeparator());
            addInfoRow(marksPanel, "Mid Term:", String.valueOf(mark.getMid()), false);
            addInfoRow(marksPanel, "Final Exam:", String.valueOf(mark.getFinalExam()), false);
            marksPanel.add(new JSeparator()); marksPanel.add(new JSeparator());
            addInfoRow(marksPanel, "Weighted Score:", String.format("%.2f / 100", weightedScore), true);
            addInfoRow(marksPanel, gradePolicyLabel, finalGrade, true);
        } else {
            marksPanel.setLayout(new BorderLayout());
            marksPanel.add(new JLabel("Marks for this subject have not been recorded yet."));
        }
        return marksPanel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value, boolean isValueBold) {
        JLabel valueLabel = new JLabel(value);
        if (isValueBold) {
            valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD));
        }
        panel.add(new JLabel(label));
        panel.add(valueLabel);
    }

    private JPanel createAttendanceDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My Full Attendance Log"));

        List<String[]> allRecords = AttendanceManager.getAttendanceRecords();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        for (String[] row : allRecords) {
            if (row.length >= 2 && row[0].trim().equalsIgnoreCase(username.trim())) {
                String timestamp = row[1];
                String date = timestamp.split(" ")[0];
                String time = timestamp.split(" ")[1];
                String section = row.length > 2 ? row[2] : "N/A";
                String formattedEntry = String.format("Date: %s | Time: %s | Section: %s", date, time, section);
                listModel.addElement(formattedEntry);
            }
        }
        
        JLabel summaryLabel = new JLabel("Total Days Logged: " + listModel.getSize());
        panel.add(summaryLabel, BorderLayout.NORTH);

        if (listModel.isEmpty()) {
            panel.add(new JLabel("No attendance records found."), BorderLayout.CENTER);
        } else {
            JList<String> attendanceList = new JList<>(listModel);
            attendanceList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            panel.add(new JScrollPane(attendanceList), BorderLayout.CENTER);
        }
        return panel;
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