package gui;

import model.Mark;
import model.User;
import util.CSVManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MarksEntryFrame extends JFrame {
    private static final int STUDENTS_PER_PAGE = 10;
    private final List<User> allStudents = new ArrayList<>(); 
    private Map<String, Mark> existingMarksMap = new HashMap<>();
    private final StudentMarksTableModel tableModel;
    private final String marksType;
    private final String classIdentifier; 
    private final JButton prevBtn, nextBtn, saveButton, cancelButton;
    private int currentPage = 0;
    private final Map<String, Integer> allEditedMarks = new HashMap<>();
    private final JTable table;

    public MarksEntryFrame(String type, String classIdentifier) {
        this.marksType = type;
        this.classIdentifier = classIdentifier;
        setTitle("Enter " + capitalize(marksType) + " Marks - Class: " + classIdentifier);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        tableModel = new StudentMarksTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enter Marks for " + capitalize(marksType) + " - Class: " + classIdentifier));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        prevBtn = new JButton("<< Previous");
        nextBtn = new JButton("Next >>");
        paginationPanel.add(prevBtn);
        paginationPanel.add(nextBtn);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton("Save Marks");
        cancelButton = new JButton("Cancel");
        actionButtonsPanel.add(cancelButton);
        actionButtonsPanel.add(saveButton);

        bottomPanel.add(paginationPanel, BorderLayout.CENTER);
        bottomPanel.add(actionButtonsPanel, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setupActions();
        loadData();
        setVisible(true);
    }

    private void setupActions() {
        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateTable();
            }
        });
        nextBtn.addActionListener(e -> {
            if ((currentPage + 1) * STUDENTS_PER_PAGE < allStudents.size()) {
                currentPage++;
                updateTable();
            }
        });
        saveButton.addActionListener(e -> saveMarks());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        try {
            List<User> allUsers = CSVManager.loadUsers("data/users.csv");
            
            for (User user : allUsers) {
                if ("student".equalsIgnoreCase(user.getRole())) {
                    allStudents.add(user);
                }
            }
            
            List<Mark> existingMarks = CSVManager.loadMarks("data/marks.csv");
            // This map is now used only for pre-filling, the save logic is smarter
            this.existingMarksMap = existingMarks.stream()
                .filter(m -> m.getSubject().equals(classIdentifier)) // Filter for current class
                .collect(Collectors.toMap(Mark::getUsername, Function.identity(), (a,b) -> a)); 

            updateTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load student or marks data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, allStudents.size());
        List<User> studentsOnPage = allStudents.subList(start, end);
        tableModel.setStudents(studentsOnPage);

        int maxPage = (allStudents.size() -1) / STUDENTS_PER_PAGE;
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage);
    }
    
    private void saveMarks() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        if (allEditedMarks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No marks were entered or changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            // **FIX APPLIED HERE**: Pass the classIdentifier to the updated CSVManager method.
            CSVManager.batchUpdateMarks("data/marks.csv", classIdentifier, marksType, allEditedMarks);
            JOptionPane.showMessageDialog(this, "Marks saved successfully for Class " + classIdentifier + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Invalid number entered. Please correct the marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private class StudentMarksTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Registration No", "Username", "Mark"};
        private List<User> studentsOnPage = new ArrayList<>();

        public void setStudents(List<User> students) {
            this.studentsOnPage = students;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() { return studentsOnPage.size(); }
        @Override
        public int getColumnCount() { return columnNames.length; }
        @Override
        public String getColumnName(int column) { return columnNames[column]; }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) { return columnIndex == 2; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User student = studentsOnPage.get(rowIndex);
            switch (columnIndex) {
                case 0: return student.getRegNo();
                case 1: return student.getUsername();
                case 2:
                    if (allEditedMarks.containsKey(student.getUsername())) {
                        return allEditedMarks.get(student.getUsername());
                    }
                    // Uses the map that was pre-filtered for the current class
                    Mark existingMark = existingMarksMap.get(student.getUsername());
                    if (existingMark != null) {
                        switch (marksType.toLowerCase()) {
                            case "quiz": return existingMark.getQuiz();
                            case "assignment": return existingMark.getAssignment();
                            case "mid": return existingMark.getMid();
                            case "final": return existingMark.getFinalExam();
                        }
                    }
                    return ""; 
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                User student = studentsOnPage.get(rowIndex);
                try {
                    String valueStr = aValue.toString();
                    if (valueStr.isEmpty()) {
                        allEditedMarks.remove(student.getUsername());
                    } else {
                        int mark = Integer.parseInt(valueStr);
                        allEditedMarks.put(student.getUsername(), mark);
                    }
                } catch (NumberFormatException e) {
                    // Let save button handle overall validation
                }
            }
        }
    }
}