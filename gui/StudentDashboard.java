package gui;

import model.Mark;
import util.CSVManager;
import util.GradeCalculator;
import util.AttendanceManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Optional;

public class StudentDashboard extends JFrame {

    private String username;
    private List<Mark> studentMarks; // Store all marks for the logged-in student

    private JList<String> subjectList;
    private DefaultListModel<String> subjectListModel;
    private JPanel detailPanel; // Panel to show marks and attendance for selected subject

    public StudentDashboard(String username) {
        this.username = username;
        setTitle("Student Dashboard - " + username);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Load student's marks data once to check against later
        try {
            List<Mark> allMarks = CSVManager.loadMarks("data/marks.csv");
            studentMarks = allMarks.stream()
                                   .filter(m -> m.getUsername().equals(username))
                                   .collect(Collectors.toList());
        } catch (IOException e) {
            studentMarks = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Could not load existing marks data: " + e.getMessage(), "Data Error", JOptionPane.ERROR_MESSAGE);
        }

        add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                              createSubjectListPanel(),
                                              createDetailPanelPlaceholder());
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        add(createFooterPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Your Academic Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        return headerPanel;
    }

    private JScrollPane createSubjectListPanel() {
        subjectListModel = new DefaultListModel<>();
        
        // This list represents all subjects available in the portal.
        String[] allAvailableSubjects = {"CY1121 - OOP Lab", "CY2311 - DLD Lab", "CY1123 - OOP"};
        for (String subject : allAvailableSubjects) {
            subjectListModel.addElement(subject);
        }

        subjectList = new JList<>(subjectListModel);
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subjectList.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        // This listener uses the imports. If this code is present, the warnings should disappear.
        subjectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && subjectList.getSelectedValue() != null) {
                updateDetailPanel(subjectList.getSelectedValue());
            }
        });

        JScrollPane scrollPane = new JScrollPane(subjectList);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "My Subjects",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        return scrollPane;
    }

    private JPanel createDetailPanelPlaceholder() {
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Subject Details",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        JLabel placeholder = new JLabel("Select a subject from the left to see details.", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        detailPanel.add(placeholder, BorderLayout.CENTER);
        return detailPanel;
    }

    private void updateDetailPanel(String selectedSubjectName) {
        detailPanel.removeAll();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));

        String selectedSubjectCode = selectedSubjectName.split(" - ")[0];

        Optional<Mark> selectedMarkOpt = studentMarks.stream()
                                        .filter(m -> m.getSubject().equals(selectedSubjectCode))
                                        .findFirst();

        JPanel marksPanel = new JPanel();
        marksPanel.setLayout(new GridLayout(0, 2, 10, 8));
        marksPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Marks for " + selectedSubjectName,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        if (selectedMarkOpt.isPresent()) {
            Mark selectedMark = selectedMarkOpt.get();
            int total = selectedMark.getQuiz() + selectedMark.getAssignment() + selectedMark.getMid() + selectedMark.getFinalExam();
            String grade = GradeCalculator.calculateAbsolute(total);

            marksPanel.add(new JLabel("Quiz:"));
            marksPanel.add(new JLabel(String.valueOf(selectedMark.getQuiz())));
            marksPanel.add(new JLabel("Assignment:"));
            marksPanel.add(new JLabel(String.valueOf(selectedMark.getAssignment())));
            marksPanel.add(new JLabel("Mid Term:"));
            marksPanel.add(new JLabel(String.valueOf(selectedMark.getMid())));
            marksPanel.add(new JLabel("Final Exam:"));
            marksPanel.add(new JLabel(String.valueOf(selectedMark.getFinalExam())));
            marksPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
            marksPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
            marksPanel.add(new JLabel("Total Marks:"));
            JLabel totalLabel = new JLabel(String.valueOf(total));
            totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            marksPanel.add(totalLabel);
            marksPanel.add(new JLabel("Calculated Grade:"));
            JLabel gradeLabel = new JLabel(grade);
            gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            marksPanel.add(gradeLabel);
        } else {
            JLabel noMarksLabel = new JLabel("Marks for this subject have not been recorded yet.");
            noMarksLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            marksPanel.setLayout(new BorderLayout());
            marksPanel.add(noMarksLabel, BorderLayout.CENTER);
        }
        
        detailPanel.add(marksPanel);
        detailPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailPanel.add(createStudentAttendancePanel());

        detailPanel.revalidate();
        detailPanel.repaint();
    }

    private JPanel createStudentAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
         panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "My Full Attendance Log",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        List<String[]> allRecords = AttendanceManager.getAttendanceRecords();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        int presentDays = 0;

        for (String[] row : allRecords) {
            if (row.length >= 2 && row[0].trim().equalsIgnoreCase(username.trim())) {
                String timestamp = row[1];
                String sectionInfo = row.length > 2 ? row[2] : "N/A";
                listModel.addElement("Date: " + timestamp.split(" ")[0] + " | Time: " + timestamp.split(" ")[1] + " | Section: " + sectionInfo);
                presentDays++;
            }
        }
        
        JLabel summaryLabel = new JLabel("Total Days Logged: " + presentDays + ". (Note: This shows all attendance, not specific to the selected subject.)");
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panel.add(summaryLabel, BorderLayout.NORTH);

        if (listModel.isEmpty()) {
            panel.add(new JLabel("  No attendance records found."), BorderLayout.CENTER);
        } else {
            JList<String> attendanceList = new JList<>(listModel);
            attendanceList.setFont(new Font("Monospaced", Font.PLAIN, 12));
            panel.add(new JScrollPane(attendanceList), BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        southPanel.add(logoutButton);
        return southPanel;
    }
}