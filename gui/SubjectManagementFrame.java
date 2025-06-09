package gui;

import model.Subject;
import util.CSVManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubjectManagementFrame extends JFrame {
    private class SubjectTableModel extends AbstractTableModel {
        private final List<Subject> subjectList;
        private final String[] columnNames = {"Subject Code", "Subject Name"};
        public SubjectTableModel(List<Subject> subjectList) { this.subjectList = subjectList; }
        @Override public int getRowCount() { return subjectList.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int c) { return columnNames[c]; }
        @Override public Object getValueAt(int r, int c) {
            Subject s = subjectList.get(r);
            return (c == 0) ? s.getCode() : s.getName();
        }
        public List<Subject> getSubjectList() { return subjectList; }
        public Subject getSubjectAt(int r) { return subjectList.get(r); }
        public void addSubject(Subject s) { subjectList.add(s); fireTableRowsInserted(subjectList.size()-1, subjectList.size()-1); }
        public void removeSubject(int r) { subjectList.remove(r); fireTableRowsDeleted(r, r); }
        public void updateSubject(int r, Subject s) { subjectList.set(r, s); fireTableRowsUpdated(r, r); }
    }

    private JTable subjectTable;
    private SubjectTableModel tableModel;
    private final String subjectsFilePath = "data/subjects.csv";

    public SubjectManagementFrame() {
        setTitle("Subject Management");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        List<Subject> subjectList;
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
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addSubject());
        editButton.addActionListener(e -> editSubject());
        deleteButton.addActionListener(e -> deleteSubject());

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
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Subject Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Subject Name:"));
        panel.add(nameField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (!code.isEmpty() && !name.isEmpty()) {
                tableModel.addSubject(new Subject(code, name));
                saveSubjectsToFile(); // Save automatically
            } else {
                JOptionPane.showMessageDialog(this, "Code and name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            Subject subject = tableModel.getSubjectAt(selectedRow);
            JTextField codeField = new JTextField(subject.getCode());
            JTextField nameField = new JTextField(subject.getName());
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Subject Code:"));
            panel.add(codeField);
            panel.add(new JLabel("Subject Name:"));
            panel.add(nameField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Subject", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                Subject updatedSubject = new Subject(codeField.getText().trim(), nameField.getText().trim());
                tableModel.updateSubject(selectedRow, updatedSubject);
                saveSubjectsToFile(); // Save automatically
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
                saveSubjectsToFile(); // Save automatically
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
}