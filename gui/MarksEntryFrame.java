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
    private final Map<String, Integer> allEditedMarks = new HashMap<>();

    private final StudentMarksTableModel tableModel;
    private final JTable table;
    private final String marksType;
    private final String classIdentifier;
    
    private final JButton prevBtn;
    private final JButton nextBtn;
    private int currentPage = 0;

    public MarksEntryFrame(String type, String classIdentifier) {
        this.marksType = type;
        this.classIdentifier = classIdentifier;
        
        setTitle("Enter " + capitalize(marksType) + " Marks - Class: " + classIdentifier);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Setup UI components
        tableModel = new StudentMarksTableModel();
        table = new JTable(tableModel);
        prevBtn = new JButton("<< Previous");
        nextBtn = new JButton("Next >>");

        setupUI();
        setupActions();
        
        loadData();
        setVisible(true);
    }

    /**
     * Configures the main layout and adds all UI components to the frame.
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Configure table properties
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Enter Marks for " + capitalize(marksType)));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Creates the bottom panel containing pagination and action buttons.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Pagination
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.add(prevBtn);
        paginationPanel.add(nextBtn);

        // Action Buttons
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Marks");
        JButton cancelButton = new JButton("Cancel");
        actionButtonsPanel.add(cancelButton);
        actionButtonsPanel.add(saveButton);

        // Add listeners for save and cancel
        saveButton.addActionListener(e -> saveMarks());
        cancelButton.addActionListener(e -> dispose());

        bottomPanel.add(paginationPanel, BorderLayout.CENTER);
        bottomPanel.add(actionButtonsPanel, BorderLayout.EAST);
        return bottomPanel;
    }

    /**
     * Sets up action listeners for pagination buttons.
     */
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
    }

    /**
     * Loads student and marks data from CSV files.
     */
    private void loadData() {
        try {
            // Load all users and filter for students using a stream
            List<User> allUsers = CSVManager.loadUsers("data/users.csv");
            allStudents.addAll(allUsers.stream()
                .filter(user -> "student".equalsIgnoreCase(user.getRole()))
                .collect(Collectors.toList()));
            
            // Load existing marks and create a map for quick lookups
            List<Mark> existingMarks = CSVManager.loadMarks("data/marks.csv");
            this.existingMarksMap = existingMarks.stream()
                .filter(m -> m.getSubject().equals(classIdentifier))
                .collect(Collectors.toMap(Mark::getUsername, Function.identity(), (a, b) -> a));

            updateTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load student or marks data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the table to show the students for the current page.
     */
    private void updateTable() {
        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, allStudents.size());
        
        tableModel.setStudents(allStudents.subList(start, end));

        int maxPage = (allStudents.size() - 1) / STUDENTS_PER_PAGE;
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage);
    }
    
    /**
     * Saves the edited marks to the CSV file.
     */
    private void saveMarks() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        if (allEditedMarks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No marks were entered or changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            CSVManager.batchUpdateMarks("data/marks.csv", classIdentifier, marksType, allEditedMarks);
            JOptionPane.showMessageDialog(this, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error saving marks. Please check input.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String capitalize(String s) {
        return (s == null || s.isEmpty()) ? "" : s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * The custom table model for displaying student marks.
     */
    private class StudentMarksTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Registration No", "Username", "Mark"};
        private List<User> studentsOnPage = new ArrayList<>();

        public void setStudents(List<User> students) {
            this.studentsOnPage = students;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return studentsOnPage.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }
        @Override public boolean isCellEditable(int row, int col) { return col == 2; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User student = studentsOnPage.get(rowIndex);
            switch (columnIndex) {
                case 0: return student.getRegNo();
                case 1: return student.getUsername();
                case 2:
                    // Priority 1: Check for unsaved, edited marks.
                    if (allEditedMarks.containsKey(student.getUsername())) {
                        return allEditedMarks.get(student.getUsername());
                    }
                    
                    // Priority 2: Check for previously saved marks from the file.
                    Mark existingMark = existingMarksMap.get(student.getUsername());
                    if (existingMark != null) {
                        return switch (marksType.toLowerCase()) {
                            case "quiz" -> existingMark.getQuiz();
                            case "assignment" -> existingMark.getAssignment();
                            case "mid" -> existingMark.getMid();
                            case "final" -> existingMark.getFinalExam();
                            default -> ""; // Should not happen
                        };
                    }
                    return ""; // Return empty if no mark is found.
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                User student = studentsOnPage.get(rowIndex);
                try {
                    String valueStr = aValue.toString().trim();
                    if (valueStr.isEmpty()) {
                        allEditedMarks.remove(student.getUsername());
                    } else {
                        allEditedMarks.put(student.getUsername(), Integer.parseInt(valueStr));
                    }
                } catch (NumberFormatException e) {
                    // Let the save button handle the validation message.
                }
            }
        }
    }
}