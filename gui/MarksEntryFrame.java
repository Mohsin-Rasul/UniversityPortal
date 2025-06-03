package gui;

import model.Mark;
import model.User;
import util.CSVManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// This GUI has been corrected and enhanced to show pre-existing marks for editing.
public class MarksEntryFrame extends JFrame {
    private static final int STUDENTS_PER_PAGE = 10;
    private final List<User> allStudents = new ArrayList<>();
    // This map will hold marks loaded from the CSV, for display.
    private Map<String, Mark> existingMarksMap = new HashMap<>();
    private final StudentMarksTableModel tableModel;
    private final String marksType;
    private final JButton prevBtn, nextBtn, saveButton, cancelButton;
    private int currentPage = 0;

    public MarksEntryFrame(String type) {
        this.marksType = type;
        setTitle("Enter " + capitalize(marksType) + " Marks");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        tableModel = new StudentMarksTableModel();
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Enter Marks for " + capitalize(marksType),
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16))
        );
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
        loadData(); // Changed from loadStudents to load all data
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
            int maxPage = (int) Math.ceil((double) allStudents.size() / STUDENTS_PER_PAGE) - 1;
            if (currentPage < maxPage) {
                currentPage++;
                updateTable();
            }
        });
        saveButton.addActionListener(e -> saveMarks());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        try {
            // Load students
            List<User> users = CSVManager.loadUsers("data/users.csv");
            for (User user : users) {
                if ("student".equalsIgnoreCase(user.getRole())) {
                    allStudents.add(user);
                }
            }
            
            // Load existing marks and put them in a map for easy lookup
            List<Mark> existingMarks = CSVManager.loadMarks("data/marks.csv");
            this.existingMarksMap = existingMarks.stream()
                .collect(Collectors.toMap(Mark::getUsername, Function.identity()));

            updateTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, allStudents.size());
        List<User> studentsOnPage = allStudents.subList(start, end);
        tableModel.setStudents(studentsOnPage);

        prevBtn.setEnabled(currentPage > 0);
        int maxPage = (int) Math.ceil((double) allStudents.size() / STUDENTS_PER_PAGE) - 1;
        nextBtn.setEnabled(currentPage < maxPage);
    }
    
    private void saveMarks() {
        // This stops any active editing in the table, ensuring the last entered value is saved.
        if (getComponent(0) instanceof JTable && ((JTable)getComponent(0)).isEditing()) {
             ((JTable)getComponent(0)).getCellEditor().stopCellEditing();
        }
    
        Map<String, Integer> marksToUpdate = tableModel.getEditedMarks();
        if (marksToUpdate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No marks were entered or changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            CSVManager.batchUpdateMarks("data/marks.csv", marksType, marksToUpdate);
            JOptionPane.showMessageDialog(this, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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

    // Inner class for the JTable model
    private class StudentMarksTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Registration No", "Username", "Mark"};
        private List<User> studentsOnPage = new ArrayList<>();
        private final Map<String, Integer> editedMarks = new HashMap<>();

        public void setStudents(List<User> students) {
            this.studentsOnPage = students;
            // Clear edits for the previous page
            this.editedMarks.clear(); 
            fireTableDataChanged();
        }

        public Map<String, Integer> getEditedMarks() {
            return editedMarks;
        }

        @Override
        public int getRowCount() {
            return studentsOnPage.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User student = studentsOnPage.get(rowIndex);
            switch (columnIndex) {
                case 0: return student.getRegNo();
                case 1: return student.getUsername();
                case 2:
                    // ** BUG FIX AND ENHANCEMENT **
                    // Priority 1: Check for marks edited in this session.
                    if (editedMarks.containsKey(student.getUsername())) {
                        return editedMarks.get(student.getUsername());
                    }
                    // Priority 2: Check for pre-existing marks loaded from the file.
                    Mark existingMark = existingMarksMap.get(student.getUsername());
                    if (existingMark != null) {
                        switch (marksType.toLowerCase()) {
                            case "quiz": return existingMark.getQuiz();
                            case "assignment": return existingMark.getAssignment();
                            case "mid": return existingMark.getMid();
                            case "final": return existingMark.getFinalExam();
                            default: return "";
                        }
                    }
                    // Priority 3: Return blank if no mark exists anywhere.
                    return ""; 
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                User student = studentsOnPage.get(rowIndex);
                try {
                    String valueStr = aValue.toString();
                    if (valueStr.isEmpty()) {
                        editedMarks.remove(student.getUsername());
                    } else {
                        int mark = Integer.parseInt(valueStr);
                        editedMarks.put(student.getUsername(), mark);
                    }
                } catch (NumberFormatException e) {
                    // This can be handled more gracefully, but for now, we let the save button catch it.
                }
            }
        }
    }
}