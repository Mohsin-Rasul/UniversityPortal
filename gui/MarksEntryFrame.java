package gui;

import model.Mark;
import model.MarkUpdate;
import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MarksEntryFrame extends JFrame {
    private static final int STUDENTS_PER_PAGE = 10;
    private final List<User> sectionStudents = new ArrayList<>();
    private List<Mark> existingMarksList = new ArrayList<>();
    
    private final List<MarkUpdate> allEditedMarks = new ArrayList<>();
    private List<User> usersOnCurrentPage = new ArrayList<>();
    private List<JTextField> fieldsOnCurrentPage = new ArrayList<>();

    private final String marksType;
    private final String sectionIdentifier;
    private final JButton prevBtn, nextBtn, saveButton, cancelButton;
    private int currentPage = 0;
    private final JPanel listPanel;

    public MarksEntryFrame(String type, String sectionIdentifier) {
        this.marksType = type;
        this.sectionIdentifier = sectionIdentifier;
        setTitle("Enter " + capitalize(marksType) + " Marks - Section " + sectionIdentifier);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
        headerPanel.add(new JLabel("Registration No"));
        headerPanel.add(new JLabel("Username"));
        headerPanel.add(new JLabel("Mark"));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        listPanel = new JPanel(new GridLayout(STUDENTS_PER_PAGE, 3, 8, 6));
        listPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        mainPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

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
            commitEditsFromCurrentPage();
            if (currentPage > 0) {
                currentPage--;
                displayCurrentPage();
            }
        });
        nextBtn.addActionListener(e -> {
            commitEditsFromCurrentPage();
            if ((currentPage + 1) * STUDENTS_PER_PAGE < sectionStudents.size()) {
                currentPage++;
                displayCurrentPage();
            }
        });
        saveButton.addActionListener(e -> saveMarks());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadData() {
        try {
            List<User> allUsers = CSVManager.loadUsers("data/users.csv");
            sectionStudents.clear();
            for (User user : allUsers) {
                if ("student".equalsIgnoreCase(user.getRole())) {
                    String regNo = user.getRegNo();
                    try {
                        int regNoSuffix = Integer.parseInt(regNo.substring(7));
                        if ("A".equals(sectionIdentifier) && regNoSuffix >= 1 && regNoSuffix <= 50) {
                            sectionStudents.add(user);
                        } else if ("B".equals(sectionIdentifier) && regNoSuffix >= 51 && regNoSuffix <= 100) {
                            sectionStudents.add(user);
                        }
                    } catch (Exception ex) {}
                }
            }
            existingMarksList = CSVManager.loadMarks("data/marks.csv");
            displayCurrentPage();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCurrentPage() {
        listPanel.removeAll();
        usersOnCurrentPage.clear();
        fieldsOnCurrentPage.clear();

        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, sectionStudents.size());
        
        usersOnCurrentPage.addAll(sectionStudents.subList(start, end));

        for (User student : usersOnCurrentPage) {
            listPanel.add(new JLabel(student.getRegNo()));
            listPanel.add(new JLabel(student.getUsername()));
            JTextField markField = new JTextField();
            markField.setText(findMarkForStudent(student.getUsername()));
            fieldsOnCurrentPage.add(markField);
            listPanel.add(markField);
        }
        
        listPanel.revalidate();
        listPanel.repaint();

        int maxPage = (sectionStudents.size() - 1) / STUDENTS_PER_PAGE;
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage && sectionStudents.size() > STUDENTS_PER_PAGE);
    }

    /**
     * THIS METHOD IS NOW FIXED.
     * It correctly finds the specific mark from the arrays based on the type (e.g., "quiz1").
     */
    private String findMarkForStudent(String username) {
        for (MarkUpdate editedMark : allEditedMarks) {
            if (editedMark.getUsername().equals(username)) {
                return String.valueOf(editedMark.getMark());
            }
        }
        for (Mark existingMark : existingMarksList) {
            if (existingMark.getUsername().equals(username)) {
                String typeLower = marksType.toLowerCase();
                if (typeLower.startsWith("quiz")) {
                    int quizNum = Integer.parseInt(typeLower.replace("quiz", "")) - 1;
                    return String.valueOf(existingMark.getQuizzes()[quizNum]);
                } else if (typeLower.startsWith("assignment")) {
                    int assignNum = Integer.parseInt(typeLower.replace("assignment", "")) - 1;
                    return String.valueOf(existingMark.getAssignments()[assignNum]);
                } else if (typeLower.equals("mid")) {
                    return String.valueOf(existingMark.getMid());
                } else if (typeLower.equals("final")) {
                    return String.valueOf(existingMark.getFinalExam());
                }
            }
        }
        return "0"; // Return 0 if no mark found
    }

    private void commitEditsFromCurrentPage() {
        for (int i = 0; i < usersOnCurrentPage.size(); i++) {
            User student = usersOnCurrentPage.get(i);
            JTextField field = fieldsOnCurrentPage.get(i);
            String valueStr = field.getText().trim();
            String username = student.getUsername();

            allEditedMarks.removeIf(mu -> mu.getUsername().equals(username));
            
            if (!valueStr.isEmpty()) {
                try {
                    int mark = Integer.parseInt(valueStr);
                    allEditedMarks.add(new MarkUpdate(username, mark));
                } catch (NumberFormatException e) {
                    // Invalid numbers will be caught during the final save
                }
            }
        }
    }
    
    private void saveMarks() {
        commitEditsFromCurrentPage();

        if (allEditedMarks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No marks were entered or changed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            for (MarkUpdate mu : allEditedMarks) {
                Integer.parseInt(String.valueOf(mu.getMark()));
            }
            CSVManager.batchUpdateMarks("data/marks.csv", marksType, allEditedMarks);
            JOptionPane.showMessageDialog(this, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "An invalid number was entered. Please correct the marks.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.matches(".*\\d.*")) {
            return s.replaceAll("(\\d)", " $1").replace("quiz", "Quiz").replace("assignment", "Assignment");
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}