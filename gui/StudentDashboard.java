package gui;

import model.Mark;
import util.CSVManager;
import util.GradeCalculator;
import util.AttendanceManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;

public class StudentDashboard extends JFrame {

    private final String username;
    private List<Mark> studentMarks;

    private final JPanel detailPanel; // The panel on the right that shows details

    public StudentDashboard(String username) {
        this.username = username;
        this.studentMarks = new ArrayList<>();
        
        setTitle("Student Dashboard - " + username);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        loadStudentMarks(); // Load data first

        // Create main UI components
        detailPanel = createDetailPanel();
        JScrollPane subjectListPanel = createSubjectListPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, subjectListPanel, detailPanel);
        splitPane.setDividerLocation(250);

        add(createHeaderPanel("Your Academic Overview"), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Loads the student's marks from the CSV file.
     */
    private void loadStudentMarks() {
        try {
            List<Mark> allMarks = CSVManager.loadMarks("data/marks.csv");
            studentMarks = allMarks.stream()
                .filter(m -> m.getUsername().equals(username))
                .collect(Collectors.toList());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load marks data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the header panel with a title.
     */
    private JPanel createHeaderPanel(String title) {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    /**
     * Creates the scrollable list of subjects on the left.
     */
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

        // Add a listener to update the detail view when a subject is selected
        subjectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                updateDetailPanelContent(subjectList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(subjectList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Subjects"));
        return scrollPane;
    }

    /**
     * Creates the initial placeholder for the detail panel on the right.
     */
    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
        JLabel placeholder = new JLabel("Select a subject from the left to see details.", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Updates the content of the detail panel based on the selected subject.
     */
    private void updateDetailPanelContent(String selectedSubjectName) {
        detailPanel.removeAll();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        String subjectCode = selectedSubjectName.split(" - ")[0];
        Optional<Mark> selectedMark = studentMarks.stream()
            .filter(m -> m.getSubject().equals(subjectCode))
            .findFirst();

        // Add the marks and attendance panels
        detailPanel.add(createMarksDisplayPanel(selectedMark, selectedSubjectName));
        detailPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailPanel.add(createAttendanceDisplayPanel());

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    /**
     * Creates the panel that displays the detailed marks breakdown.
     */
    private JPanel createMarksDisplayPanel(Optional<Mark> markOpt, String subjectName) {
        JPanel marksPanel = new JPanel(new GridLayout(0, 2, 10, 8));
        marksPanel.setBorder(BorderFactory.createTitledBorder("Marks for " + subjectName));

        if (markOpt.isPresent()) {
            Mark mark = markOpt.get();
            int total = mark.getQuiz() + mark.getAssignment() + mark.getMid() + mark.getFinalExam();
            String grade = GradeCalculator.calculateAbsolute(total);

            // Add each mark using a helper method to reduce repeated code
            addInfoRow(marksPanel, "Quiz:", String.valueOf(mark.getQuiz()), false);
            addInfoRow(marksPanel, "Assignment:", String.valueOf(mark.getAssignment()), false);
            addInfoRow(marksPanel, "Mid Term:", String.valueOf(mark.getMid()), false);
            addInfoRow(marksPanel, "Final Exam:", String.valueOf(mark.getFinalExam()), false);
            marksPanel.add(new JSeparator());
            marksPanel.add(new JSeparator());
            addInfoRow(marksPanel, "Total Marks:", String.valueOf(total), true);
            addInfoRow(marksPanel, "Calculated Grade:", grade, true);
        } else {
            marksPanel.setLayout(new BorderLayout());
            marksPanel.add(new JLabel("Marks for this subject have not been recorded yet."));
        }
        return marksPanel;
    }
    
    /**
     * A helper to add a label and a value to a panel, with an option for bold text.
     */
    private void addInfoRow(JPanel panel, String label, String value, boolean isValueBold) {
        JLabel valueLabel = new JLabel(value);
        if (isValueBold) {
            valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD));
        }
        panel.add(new JLabel(label));
        panel.add(valueLabel);
    }

    /**
     * Creates the panel that displays the student's attendance log.
     */
    private JPanel createAttendanceDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("My Full Attendance Log"));

        List<String[]> allRecords = AttendanceManager.getAttendanceRecords();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        
        // Use a stream to filter and format attendance records
        allRecords.stream()
            .filter(row -> row.length >= 2 && row[0].trim().equalsIgnoreCase(username.trim()))
            .map(row -> {
                String timestamp = row[1];
                String section = row.length > 2 ? row[2] : "N/A";
                return String.format("Date: %s | Time: %s | Section: %s", timestamp.split(" ")[0], timestamp.split(" ")[1], section);
            })
            .forEach(listModel::addElement);
        
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
    
    /**
     * Creates the footer panel with a logout button.
     */
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