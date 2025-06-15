import model.Mark;
import model.Subject;
import util.AttendanceManager;
import util.CSVManager;
import util.ConfigManager;
import util.GradeCalculator;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final String username;
    private ArrayList<Mark> allMarks;
    private final JPanel detailPanel;
    private String gradingPolicy;

    private static final double QUIZ_WEIGHT = 0.20;
    private static final double ASSIGNMENT_WEIGHT = 0.20;
    private static final double MID_WEIGHT = 0.25;
    private static final double FINAL_WEIGHT = 0.35;

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
        final DefaultListModel<Subject> subjectListModel = new DefaultListModel<>();
        try {
            ArrayList<Subject> subjects = CSVManager.loadSubjects("data/subjects.csv");
            for (Subject s : subjects) {
                subjectListModel.addElement(s);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        final JList<Subject> subjectList = new JList<>(subjectListModel);
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subjectList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        subjectList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                    updateDetailPanelContent(subjectList.getSelectedValue());
                }
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

    private void updateDetailPanelContent(Subject selectedSubject) {
        detailPanel.removeAll();
        detailPanel.setLayout(new GridLayout(0, 1, 10, 15));

        String subjectCode = selectedSubject.getCode();

        Mark studentMark = null;
        for (Mark mark : allMarks) {
            if (mark.getSubject().equalsIgnoreCase(subjectCode) && mark.getUsername().equals(this.username)) {
                studentMark = mark;
                break;
            }
        }

        ArrayList<Double> allWeightedScores = new ArrayList<>();
        if ("relative".equals(gradingPolicy)) {
            for (Mark mark : allMarks) {
                if (mark.getSubject().equalsIgnoreCase(subjectCode)) {
                    allWeightedScores.add(calculateWeightedScore(mark));
                }
            }
        }

        detailPanel.add(createMarksDisplayPanel(studentMark, selectedSubject.toString(), allWeightedScores));
        detailPanel.add(createAttendanceDisplayPanel(selectedSubject));

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private double calculateWeightedScore(Mark mark) {
        if (mark == null) return 0.0;
        int totalQuizScore = 0;
        for (int score : mark.getQuizzes()) {
            totalQuizScore += score;
        }

        int totalAssignmentScore = 0;
        for (int score : mark.getAssignments()) {
            totalAssignmentScore += score;
        }

        double quizScore = (double) totalQuizScore / 40.0;
        double assignmentScore = (double) totalAssignmentScore / 40.0;
        double midScore = (double) mark.getMid() / 20.0;
        double finalScore = (double) mark.getFinalExam() / 40.0;
        return (quizScore * QUIZ_WEIGHT + assignmentScore * ASSIGNMENT_WEIGHT + midScore * MID_WEIGHT + finalScore * FINAL_WEIGHT) * 100;
    }

    private JPanel createMarksDisplayPanel(Mark mark, String subjectName, ArrayList<Double> allWeightedScores) {
        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 0, 5)); 
        mainPanel.setBorder(BorderFactory.createTitledBorder("Marks for " + subjectName));

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
                mainPanel.add(createLabelValuePairRow("Quiz " + (i + 1) + ":", String.valueOf(mark.getQuizzes()[i]), false));
            }
            mainPanel.add(new JSeparator());

            for (int i = 0; i < 4; i++) {
                mainPanel.add(createLabelValuePairRow("Assignment " + (i + 1) + ":", String.valueOf(mark.getAssignments()[i]), false));
            }
            mainPanel.add(new JSeparator());

            mainPanel.add(createLabelValuePairRow("Mid Term:", String.valueOf(mark.getMid()), false));
            mainPanel.add(createLabelValuePairRow("Final Exam:", String.valueOf(mark.getFinalExam()), false));
            mainPanel.add(new JSeparator());
            
            mainPanel.add(createLabelValuePairRow("Weighted Score:", String.format("%.2f / 100", weightedScore), true));
            mainPanel.add(createLabelValuePairRow(gradePolicyLabel, finalGrade, true));

        } else {
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(new JLabel("Marks for this subject have not been recorded yet."));
        }
        return mainPanel;
    }
    
    private JPanel createLabelValuePairRow(String labelText, String valueText, boolean isValueBold) {
        JPanel rowPanel = new JPanel(new GridLayout(1, 2)); 
        rowPanel.add(new JLabel(labelText));
        
        JLabel valueLabel = new JLabel(valueText);
        if (isValueBold) {
            valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD));
        }
        rowPanel.add(valueLabel);
        return rowPanel;
    }

    private JPanel createAttendanceDisplayPanel(Subject selectedSubject) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Attendance Log for " + selectedSubject.getCode()));

        List<String[]> allRecords = AttendanceManager.getAttendanceRecords();
        List<Object[]> subjectSpecificData = new ArrayList<>();
        List<String> uniqueDates = new ArrayList<>();
        int serialNumber = 1;

        for (String[] row : allRecords) {
            boolean usernameMatch = row.length >= 1 && row[0].trim().equalsIgnoreCase(username.trim());
            boolean subjectMatch = row.length >= 3 && row[2].trim().equalsIgnoreCase(selectedSubject.getCode().trim());

            if (usernameMatch && subjectMatch) {
                String timestamp = row[1];
                String date = timestamp.split(" ")[0];
                if (!uniqueDates.contains(date)) {
                    uniqueDates.add(date);
                    subjectSpecificData.add(new Object[]{serialNumber++, date, "Present"});
                }
            }
        }

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.add(new JLabel("Total Classes Attended: " + subjectSpecificData.size()));
        panel.add(summaryPanel, BorderLayout.NORTH);

        if (subjectSpecificData.isEmpty()) {
            panel.add(new JLabel("No attendance records found for this subject.", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            String[] columnNames = {"Sr. No.", "Date", "Status"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (Object[] rowData : subjectSpecificData) {
                tableModel.addRow(rowData);
            }
            JTable attendanceTable = new JTable(tableModel);
            attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            attendanceTable.setFillsViewportHeight(true);
            panel.add(new JScrollPane(attendanceTable), BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });
        southPanel.add(logoutButton);
        return southPanel;
    }
}