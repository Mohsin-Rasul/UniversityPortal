import model.Subject;
import util.CSVManager;
import util.ConfigManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TeacherDashboard extends JFrame {
    private final String teacherUsername;
    private JComboBox<Subject> classSelector;
    private JComboBox<String> gradingPolicySelector;
    private JLabel marksManagementTitle;

    public TeacherDashboard(String username) {
        this.teacherUsername = username;
        setTitle("Teacher Dashboard - " + teacherUsername);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        updateDynamicTitles();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Teacher Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 10, 15)); // 0 rows, 1 col
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        mainPanel.add(createClassSelectorPanel());
        mainPanel.add(createGradingPolicyPanel());
        mainPanel.add(createMarksPanel());
        mainPanel.add(createAttendancePanel());
        
        return mainPanel;
    }
    
    private JPanel createClassSelectorPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Select Class"));
        
        classSelector = new JComboBox<>();
        try {
            ArrayList<Subject> allSubjects = CSVManager.loadSubjects("data/subjects.csv");
            ArrayList<Subject> taughtSubjects = new ArrayList<>();
            for (Subject subject : allSubjects) {
                if (teacherUsername.equalsIgnoreCase(subject.getTeacherUsername())) {
                    taughtSubjects.add(subject);
                }
            }
            Subject[] subjectArray = taughtSubjects.toArray(new Subject[0]);
            classSelector.setModel(new DefaultComboBoxModel<>(subjectArray));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        classSelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        classSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDynamicTitles();
            }
        });

        panel.add(new JLabel("My Classes:"));
        panel.add(classSelector);
        return panel;
    }

    private void updateDynamicTitles() {
        Subject selectedSubject = (Subject) classSelector.getSelectedItem();
        if (selectedSubject != null && marksManagementTitle != null) {
            marksManagementTitle.setText("Marks Management - " + selectedSubject.getCode());
        }
    }
    
    private String getSelectedSubjectIdentifier() {
        Subject selectedSubject = (Subject) classSelector.getSelectedItem();
        if (selectedSubject != null) {
            return selectedSubject.getCode();
        }
        return "";
    }

    private JPanel createGradingPolicyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Set Grading Policy"));

        gradingPolicySelector = new JComboBox<>(new String[]{"Absolute Grading", "Relative Grading"});
        gradingPolicySelector.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        String currentPolicy = ConfigManager.loadGradingPolicy();
        if ("relative".equals(currentPolicy)) {
            gradingPolicySelector.setSelectedItem("Relative Grading");
        } else {
            gradingPolicySelector.setSelectedItem("Absolute Grading");
        }

        JButton savePolicyButton = new JButton("Save Policy");
        savePolicyButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        savePolicyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGradingPolicy();
            }
        });

        panel.add(new JLabel("Select Policy:"));
        panel.add(gradingPolicySelector);
        panel.add(savePolicyButton);
        return panel;
    }

    private void saveGradingPolicy() {
        String selection = (String) gradingPolicySelector.getSelectedItem();
        String policyToSave = "absolute";
        if ("Relative Grading".equals(selection)) {
            policyToSave = "relative";
        }
        ConfigManager.saveGradingPolicy(policyToSave);
        JOptionPane.showMessageDialog(this, "Grading policy has been updated to: " + selection, "Policy Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createMarksPanel() {
        JPanel marksOuterPanel = new JPanel(new BorderLayout());
        marksManagementTitle = new JLabel("Marks Management", SwingConstants.LEFT);
        marksManagementTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        marksManagementTitle.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        
        JPanel marksButtonsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        marksButtonsPanel.add(createStyledButton("Quiz Marks", "quiz"));
        marksButtonsPanel.add(createStyledButton("Assignment Marks", "assignment"));
        marksButtonsPanel.add(createStyledButton("Mid Term Marks", "mid"));
        marksButtonsPanel.add(createStyledButton("Final Marks", "final"));
        
        marksOuterPanel.add(marksManagementTitle, BorderLayout.NORTH);
        marksOuterPanel.add(marksButtonsPanel, BorderLayout.CENTER);
        
        JPanel titledMarksPanel = new JPanel(new BorderLayout());
        titledMarksPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        titledMarksPanel.add(marksOuterPanel, BorderLayout.CENTER);

        return titledMarksPanel;
    }

    private JPanel createAttendancePanel() {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBorder(BorderFactory.createTitledBorder("Attendance"));
        JButton startAttendanceBtn = new JButton("Start Attendance for Selected Class");
        startAttendanceBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        startAttendanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAttendanceScript();
            }
        });
        attendancePanel.add(startAttendanceBtn, BorderLayout.CENTER);
        return attendancePanel;
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

    private JButton createStyledButton(String text, String type) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sectionIdentifier = getSelectedSubjectIdentifier();
                String finalType = type;

                if ("".equals(sectionIdentifier)){
                    JOptionPane.showMessageDialog(TeacherDashboard.this, "Please select a class first.", "No Class Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (type.equals("quiz") || type.equals("assignment")) {
                    String numberStr = JOptionPane.showInputDialog(TeacherDashboard.this, "Enter " + capitalize(type) + " Number (1-4):");
                    if (numberStr != null && !numberStr.trim().isEmpty()) {
                        try {
                            int num = Integer.parseInt(numberStr.trim());
                            if (num >= 1 && num <= 4) {
                                finalType = type + num;
                            } else {
                                JOptionPane.showMessageDialog(TeacherDashboard.this, "Please enter a number between 1 and 4.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(TeacherDashboard.this, "Invalid number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        return; 
                    }
                }
                new MarksEntryFrame(finalType, sectionIdentifier);
            }
        });
        return button;
    }
    
    private void startAttendanceScript() {
        String sectionIdentifier = getSelectedSubjectIdentifier();
        if ("".equals(sectionIdentifier)){
            JOptionPane.showMessageDialog(this, "Please select a class first.", "No Class Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String scriptPath = new File("attendance/python/recognize_faces.py").getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, "--section", sectionIdentifier);
            pb.inheritIO();
            pb.start();
            JOptionPane.showMessageDialog(this, "Starting attendance for Class " + sectionIdentifier + ".\nPress 'q' in the recognition window to quit.", "Attendance Started", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                "Failed to start attendance script.\nEnsure Python is installed and in your system's PATH.",
                "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}