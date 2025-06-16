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

public class StudentDashboard extends JFrame {

    private final String username;
    private ArrayList<Mark> allMarks;
    private final JPanel detailPanel;
    private String gradingPolicy;

    public static final double QUIZ_WEIGHT = 0.20;
    public static final double ASSIGNMENT_WEIGHT = 0.20;
    public static final double MID_WEIGHT = 0.20;
    public static final double FINAL_WEIGHT = 0.40;

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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subjectListPanel, new JScrollPane(detailPanel));
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
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        String subjectCode = selectedSubject.getCode();

        Mark studentMark = null;
        for (Mark mark : allMarks) {
            if (mark.getSubject().equalsIgnoreCase(subjectCode) && mark.getUsername().equalsIgnoreCase(this.username)) {
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

        detailPanel.add(createMarksDisplayPanel(studentMark, selectedSubject, allWeightedScores));
        detailPanel.add(createGradeCalculatorPanel(studentMark));
        detailPanel.add(createAttendanceDisplayPanel(selectedSubject));

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private double calculateWeightedScore(Mark mark) {
        if (mark == null) return 0.0;
        
        double quizScore = ((double) mark.getTotalQuizScore() / 40.0) * QUIZ_WEIGHT;
        double assignmentScore = ((double) mark.getTotalAssignmentScore() / 40.0) * ASSIGNMENT_WEIGHT;
        double midScore = ((double) mark.getMid() / 20.0) * MID_WEIGHT;
        double finalScore = ((double) mark.getFinalExam() / 40.0) * FINAL_WEIGHT;

        return (quizScore + assignmentScore + midScore + finalScore) * 100;
    }
    
    private JPanel createMarksDisplayPanel(Mark mark, Subject subject, ArrayList<Double> allWeightedScores) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createTitledBorder("Marks for " + subject.getName()));

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
            
            mainPanel.add(createLabelValuePairRow("Quiz 1:", String.valueOf(mark.getQuiz1()), false));
            mainPanel.add(createLabelValuePairRow("Quiz 2:", String.valueOf(mark.getQuiz2()), false));
            mainPanel.add(createLabelValuePairRow("Quiz 3:", String.valueOf(mark.getQuiz3()), false));
            mainPanel.add(createLabelValuePairRow("Quiz 4:", String.valueOf(mark.getQuiz4()), false));
            mainPanel.add(new JSeparator());

            mainPanel.add(createLabelValuePairRow("Assignment 1:", String.valueOf(mark.getAssignment1()), false));
            mainPanel.add(createLabelValuePairRow("Assignment 2:", String.valueOf(mark.getAssignment2()), false));
            mainPanel.add(createLabelValuePairRow("Assignment 3:", String.valueOf(mark.getAssignment3()), false));
            mainPanel.add(createLabelValuePairRow("Assignment 4:", String.valueOf(mark.getAssignment4()), false));
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
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowPanel.getPreferredSize().height));
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

        ArrayList<String[]> allRecords = AttendanceManager.getAttendanceRecords();
        ArrayList<Object[]> subjectSpecificData = new ArrayList<>();
        
        // MODIFIED: Changed from uniqueDates to uniqueDateHours to track attendance per hour.
        ArrayList<String> uniqueDateHours = new ArrayList<>();
        int serialNumber = 1;

        for (String[] row : allRecords) {
            boolean usernameMatch = row.length >= 1 && row[0].trim().equalsIgnoreCase(username.trim());
            boolean subjectMatch = row.length >= 3 && row[2].trim().equalsIgnoreCase(selectedSubject.getCode().trim());

            if (usernameMatch && subjectMatch) {
                String timestamp = row[1];
                
                // --- Manual logic to get date and hour ---
                StringBuilder dateBuilder = new StringBuilder();
                StringBuilder hourBuilder = new StringBuilder();
                boolean spaceFound = false;
                for (int i = 0; i < timestamp.length(); i++) {
                    char c = timestamp.charAt(i);
                    if (c == ' ') {
                        spaceFound = true;
                        continue; // Skip the space itself
                    }
                    if (!spaceFound) {
                        dateBuilder.append(c);
                    } else {
                        // Append the two characters for the hour
                        if (hourBuilder.length() < 2) {
                            hourBuilder.append(c);
                        }
                    }
                }
                String date = dateBuilder.toString();
                String hour = hourBuilder.toString();
                String dateHourKey = date + "-" + hour; // e.g., "2025-06-16-10"

                // MODIFIED: Check against the unique date-hour key.
                if (!uniqueDateHours.contains(dateHourKey)) {
                    uniqueDateHours.add(dateHourKey);
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
                @Override public boolean isCellEditable(int row, int column) { return false; }
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
    
    private JPanel createGradeCalculatorPanel(final Mark studentMark) {
        JPanel calcPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        calcPanel.setBorder(BorderFactory.createTitledBorder("Grade Calculator"));
        calcPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, calcPanel.getPreferredSize().height * 3));

        if (studentMark == null) {
            calcPanel.setLayout(new BorderLayout());
            calcPanel.add(new JLabel("  Not available until marks are entered."));
            return calcPanel;
        }

        ArrayList<String> pendingAssessments = new ArrayList<>();
        if (studentMark.getMid() == 0) {
            pendingAssessments.add("Mid Term");
        } else if (studentMark.getFinalExam() == 0) {
            pendingAssessments.add("Final Exam");
        }

        if (pendingAssessments.isEmpty()) {
            calcPanel.setLayout(new BorderLayout());
            calcPanel.add(new JLabel("  All assessments have been graded."));
            return calcPanel;
        }

        final JTextField desiredGradeField = new JTextField();
        final JComboBox<String> targetAssessmentBox = new JComboBox<>(pendingAssessments.toArray(new String[0]));
        final JButton calculateButton = new JButton("Calculate Required Score");
        final JLabel resultLabel = new JLabel("Result will be shown here.");
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD));

        calcPanel.add(new JLabel("Desired Course Grade (%):"));
        calcPanel.add(desiredGradeField);
        calcPanel.add(new JLabel("To achieve this, I need this score on my:"));
        calcPanel.add(targetAssessmentBox);
        calcPanel.add(calculateButton);
        calcPanel.add(resultLabel);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double desiredGrade = Double.parseDouble(desiredGradeField.getText());

                    if (desiredGrade < 0 || desiredGrade > 100) {
                        JOptionPane.showMessageDialog(calcPanel, "Desired grade must be between 0 and 100.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double quizC = ((double) studentMark.getTotalQuizScore() / 40.0) * QUIZ_WEIGHT * 100;
                    double assignC = ((double) studentMark.getTotalAssignmentScore() / 40.0) * ASSIGNMENT_WEIGHT * 100;
                    double midC_forMax = (studentMark.getMid() == 0) ? (100 * MID_WEIGHT) : (((double) studentMark.getMid() / 20.0) * MID_WEIGHT * 100);
                    double finalC_forMax = (studentMark.getFinalExam() == 0) ? (100 * FINAL_WEIGHT) : (((double) studentMark.getFinalExam() / 40.0) * FINAL_WEIGHT * 100);
                    double maxAchievableGrade = quizC + assignC + midC_forMax + finalC_forMax;

                    if (desiredGrade > maxAchievableGrade) {
                        JOptionPane.showMessageDialog(calcPanel,
                                String.format("The maximum grade you can achieve is %.2f%%. Please enter a lower target.", maxAchievableGrade),
                                "Target Unachievable", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String targetAssessment = (String) targetAssessmentBox.getSelectedItem();
                    double currentWeightedSum = 0.0;
                    double targetWeight = 0.0;
                    int maxMarksForTarget = 0;

                    if ("Final Exam".equals(targetAssessment)) {
                        targetWeight = FINAL_WEIGHT;
                        maxMarksForTarget = 40;
                        currentWeightedSum = (quizC + assignC + (((double) studentMark.getMid() / 20.0) * MID_WEIGHT * 100));
                    } else if ("Mid Term".equals(targetAssessment)) {
                        targetWeight = MID_WEIGHT;
                        maxMarksForTarget = 20;
                        currentWeightedSum = (quizC + assignC); 
                    }

                    if (targetWeight > 0) {
                        double requiredPercentage = (desiredGrade - currentWeightedSum) / targetWeight;
                        double rawMarksNeeded = (requiredPercentage / 100.0) * maxMarksForTarget;
                        
                        resultLabel.setText(String.format("You need at least %.2f / %d on the %s.", rawMarksNeeded, maxMarksForTarget, targetAssessment));
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(calcPanel, "Please enter a valid number for your desired grade.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return calcPanel;
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