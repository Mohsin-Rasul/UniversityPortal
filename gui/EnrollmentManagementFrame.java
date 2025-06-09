package gui;

import model.Mark;
import model.Subject;
import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// import java.util.stream.Collectors; // No longer needed

public class EnrollmentManagementFrame extends JFrame {
    private JComboBox<Subject> subjectComboBox;
    private JList<User> studentList;
    private DefaultListModel<User> studentListModel;
    private List<User> allStudents;

    public EnrollmentManagementFrame() {
        setTitle("Student Enrollment Management");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select a Subject"));
        subjectComboBox = new JComboBox<>();
        topPanel.add(new JLabel("Subject:"));
        topPanel.add(subjectComboBox);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Select Students to Enroll"));
        studentListModel = new DefaultListModel<>();
        studentList = new JList<>(studentListModel);
        studentList.setCellRenderer(new UserCellRenderer());
        centerPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Enrollments");
        bottomPanel.add(saveButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadInitialData();
        subjectComboBox.addActionListener(e -> updateStudentList());
        saveButton.addActionListener(e -> saveEnrollments());
        
        setVisible(true);
    }
    
    private void loadInitialData() {
        try {
            List<Subject> subjects = CSVManager.loadSubjects("data/subjects.csv");
            for (Subject s : subjects) {
                subjectComboBox.addItem(s);
            }
            
            // Filter for students using a for loop
            allStudents = new ArrayList<>();
            List<User> allUsers = CSVManager.loadUsers("data/users.csv");
            for(User user : allUsers) {
                if("student".equalsIgnoreCase(user.getRole())) {
                    allStudents.add(user);
                }
            }
            
            for (User student : allStudents) {
                studentListModel.addElement(student);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load initial data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        updateStudentList();
    }

    private void updateStudentList() {
        Subject selectedSubject = (Subject) subjectComboBox.getSelectedItem();
        if (selectedSubject == null) return;
        
        try {
            List<Mark> marks = CSVManager.loadMarks("data/marks.csv");
            
            // Get enrolled usernames with a for loop
            List<String> enrolledUsernames = new ArrayList<>();
            for(Mark m : marks) {
                if(m.getSubject().equalsIgnoreCase(selectedSubject.getCode())) {
                    enrolledUsernames.add(m.getUsername());
                }
            }
            
            List<Integer> indicesToSelect = new ArrayList<>();
            for (int i = 0; i < allStudents.size(); i++) {
                if (enrolledUsernames.contains(allStudents.get(i).getUsername())) {
                    indicesToSelect.add(i);
                }
            }
            
            // Convert List<Integer> to int[]
            int[] selectedIndices = new int[indicesToSelect.size()];
            for(int i=0; i < indicesToSelect.size(); i++) {
                selectedIndices[i] = indicesToSelect.get(i);
            }
            studentList.setSelectedIndices(selectedIndices);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load enrollment data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveEnrollments() {
        Subject selectedSubject = (Subject) subjectComboBox.getSelectedItem();
        if (selectedSubject == null) return;

        List<User> selectedStudents = studentList.getSelectedValuesList();
        
        // Get usernames to enroll with a for loop
        List<String> usernamesToEnroll = new ArrayList<>();
        for(User student : selectedStudents) {
            usernamesToEnroll.add(student.getUsername());
        }

        try {
            CSVManager.enrollStudents("data/marks.csv", selectedSubject.getCode(), usernamesToEnroll);
            JOptionPane.showMessageDialog(this, "Enrollments saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class UserCellRenderer extends JCheckBox implements ListCellRenderer<User> {
        @Override
        public Component getListCellRendererComponent(JList<? extends User> list, User user, int index, boolean isSelected, boolean cellHasFocus) {
            setText(user.getUsername() + " (" + user.getRegNo() + ")");
            setSelected(isSelected);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            return this;
        }
    }
}