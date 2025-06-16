import model.Subject;
import model.User;
import util.CSVManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class SubjectManagementFrame extends JFrame {
    private class SubjectTableModel extends AbstractTableModel {
        private final ArrayList<Subject> subjectList;
        private final String[] columnNames = {"Code", "Name", "Teacher"};
        public SubjectTableModel(ArrayList<Subject> subjectList) { this.subjectList = subjectList; }
        @Override public int getRowCount() { return subjectList.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }
        @Override public Object getValueAt(int r, int c) {
            Subject s = subjectList.get(r);
            switch (c) {
                case 0: return s.getCode();
                case 1: return s.getName();
                case 2: return s.getTeacherUsername();
                default: return null;
            }
        }
        public ArrayList<Subject> getSubjectList() { return subjectList; }
        public Subject getSubjectAt(int r) { return subjectList.get(r); }
        public void addSubject(Subject s) { subjectList.add(s); fireTableRowsInserted(subjectList.size()-1, subjectList.size()-1); }
        public void removeSubject(int r) { subjectList.remove(r); fireTableRowsDeleted(r, r); }
        public void updateSubject(int r, Subject s) { subjectList.set(r, s); fireTableRowsUpdated(r, r); }
    }

    private JTable subjectTable;
    private SubjectTableModel tableModel;
    private final String subjectsFilePath = "data/subjects.csv";
    private final String usersFilePath = "data/users.csv";
    private final String marksFilePath = "data/marks.csv";

    public SubjectManagementFrame() {
        setTitle("Subject Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ArrayList<Subject> subjectList;
        try {
            subjectList = CSVManager.loadSubjects(subjectsFilePath);
        } catch (IOException e) {
            subjectList = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        tableModel = new SubjectTableModel(subjectList);
        subjectTable = new JTable(tableModel);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Subject");
        JButton editButton = new JButton("Edit Subject");
        JButton deleteButton = new JButton("Delete Subject");
        JButton enrollButton = new JButton("Enroll Students");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(enrollButton);

        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addSubject());
        editButton.addActionListener(e -> editSubject());
        deleteButton.addActionListener(e -> deleteSubject());
        enrollButton.addActionListener(e -> enrollStudents());
        
        setVisible(true);
    }
    
    private void saveSubjectsToFile() {
        try {
            CSVManager.saveSubjects(subjectsFilePath, tableModel.getSubjectList());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save subjects: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSubject() {
        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();
        JComboBox<String> teacherComboBox = getTeacherComboBox();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Teacher:"));
        panel.add(teacherComboBox);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            tableModel.addSubject(new Subject(codeField.getText(), nameField.getText(), (String)teacherComboBox.getSelectedItem()));
            saveSubjectsToFile();
        }
    }

    private void editSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            Subject subjectToEdit = tableModel.getSubjectAt(selectedRow);
            JTextField codeField = new JTextField(subjectToEdit.getCode());
            JTextField nameField = new JTextField(subjectToEdit.getName());
            JComboBox<String> teacherComboBox = getTeacherComboBox();
            teacherComboBox.setSelectedItem(subjectToEdit.getTeacherUsername());
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Code:"));
            panel.add(codeField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Teacher:"));
            panel.add(teacherComboBox);
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                tableModel.updateSubject(selectedRow, new Subject(codeField.getText(), nameField.getText(), (String)teacherComboBox.getSelectedItem()));
                saveSubjectsToFile();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    

    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this subject?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeSubject(selectedRow);
                saveSubjectsToFile();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void enrollStudents() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            Subject selectedSubject = tableModel.getSubjectAt(selectedRow);
            try {
                ArrayList<User> allUsers = CSVManager.loadUsers(usersFilePath);
                ArrayList<User> students = new ArrayList<>();
                for (User u : allUsers) {
                    if ("student".equals(u.getRole())) {
                        students.add(u);
                    }
                }

                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No students available to enroll.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                JList<User> studentJList = new JList<>(students.toArray(new User[0]));
                studentJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                studentJList.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof User) {
                            setText(((User) value).getUsername());
                        }
                        return this;
                    }
                });

                int result = JOptionPane.showConfirmDialog(this, new JScrollPane(studentJList), "Select Students to Enroll in " + selectedSubject.getCode(), JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    ArrayList<User> selectedStudents = new ArrayList<>(studentJList.getSelectedValuesList());
                    ArrayList<String> selectedUsernames = new ArrayList<>();
                    for (User u : selectedStudents) {
                        selectedUsernames.add(u.getUsername());
                    }
                    
                    CSVManager.enrollStudents(marksFilePath, selectedSubject.getCode(), selectedUsernames);
                    JOptionPane.showMessageDialog(this, "Enrolled " + selectedStudents.size() + " student(s).", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error during enrollment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to enroll students in.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JComboBox<String> getTeacherComboBox() {
        try {
            ArrayList<User> users = CSVManager.loadUsers(usersFilePath);
            ArrayList<String> teacherNames = new ArrayList<>();
            for (User u : users) {
                if ("teacher".equals(u.getRole())) {
                    teacherNames.add(u.getUsername());
                }
            }
            
            return new JComboBox<>(teacherNames.toArray(new String[0]));
        } catch (IOException e) {
            return new JComboBox<>(new String[]{"Error loading teachers"});
        }
    }
}