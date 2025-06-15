import model.Mark;
import model.MarkUpdate;
import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MarksEntryFrame extends JFrame {
    private static final int STUDENTS_PER_PAGE = 10;
    private final ArrayList<User> enrolledStudents = new ArrayList<>();
    private ArrayList<Mark> existingMarksList = new ArrayList<>();
    
    private final ArrayList<MarkUpdate> allEditedMarks = new ArrayList<>();
    private ArrayList<User> usersOnCurrentPage = new ArrayList<>();
    private ArrayList<JTextField> fieldsOnCurrentPage = new ArrayList<>();

    private final String marksType;
    private final String subjectCode;
    private final JButton prevBtn, nextBtn, saveButton, cancelButton;
    private int currentPage = 0;
    private final JPanel listPanel;

    public MarksEntryFrame(String type, String subjectCode) {
        this.marksType = type;
        this.subjectCode = subjectCode;
        setTitle("Enter " + capitalize(marksType) + " Marks - Class " + subjectCode);
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

        listPanel = new JPanel(new GridLayout(0, 3, 8, 6));
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
        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commitEditsFromCurrentPage();
                if (currentPage > 0) {
                    currentPage--;
                    displayCurrentPage();
                }
            }
        });
        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commitEditsFromCurrentPage();
                if ((currentPage + 1) * STUDENTS_PER_PAGE < enrolledStudents.size()) {
                    currentPage++;
                    displayCurrentPage();
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMarks();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadData() {
        try {
            ArrayList<User> allUsers = CSVManager.loadUsers("data/users.csv");
            existingMarksList = CSVManager.loadMarks("data/marks.csv");
            
            ArrayList<String> studentUsernamesForSubject = new ArrayList<>();
            for (Mark mark : existingMarksList) {
                if (mark.getSubject().equalsIgnoreCase(subjectCode)) {
                    if (!studentUsernamesForSubject.contains(mark.getUsername())) {
                        studentUsernamesForSubject.add(mark.getUsername());
                    }
                }
            }

            enrolledStudents.clear();
            for (User user : allUsers) {
                if (studentUsernamesForSubject.contains(user.getUsername())) {
                    enrolledStudents.add(user);
                }
            }
            
            displayCurrentPage();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayCurrentPage() {
        listPanel.removeAll();
        usersOnCurrentPage.clear();
        fieldsOnCurrentPage.clear();

        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, enrolledStudents.size());
        
        usersOnCurrentPage.addAll(new ArrayList<>(enrolledStudents.subList(start, end)));

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

        int maxPage = (enrolledStudents.size() - 1) / STUDENTS_PER_PAGE;
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < maxPage && enrolledStudents.size() > STUDENTS_PER_PAGE);
    }

    private String findMarkForStudent(String username) {
        for (MarkUpdate editedMark : allEditedMarks) {
            if (editedMark.getUsername().equals(username)) {
                return String.valueOf(editedMark.getMark());
            }
        }
        for (Mark existingMark : existingMarksList) {
            if (existingMark.getUsername().equals(username) && existingMark.getSubject().equalsIgnoreCase(subjectCode)) {
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
        return "0";
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

        int maxMark;
        String markTypeName;
        String typeLower = marksType.toLowerCase();

        if (typeLower.startsWith("quiz")) {
            maxMark = 10;
            markTypeName = "Quiz";
        } else if (typeLower.startsWith("assignment")) {
            maxMark = 10;
            markTypeName = "Assignment";
        } else if (typeLower.equals("mid")) {
            maxMark = 20;
            markTypeName = "Mid Term";
        } else if (typeLower.equals("final")) {
            maxMark = 40;
            markTypeName = "Final Exam";
        } else {
            JOptionPane.showMessageDialog(this, "Unknown mark type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (MarkUpdate mu : allEditedMarks) {
            try {
                int mark = mu.getMark();
                if (mark < 0 || mark > maxMark) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid mark for student " + mu.getUsername() + ".\n" +
                        markTypeName + " marks must be between 0 and " + maxMark + ".",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(this,
                    "An invalid number was entered for student " + mu.getUsername() + ".\nPlease correct the marks.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
        }

        try {
            CSVManager.batchUpdateMarks("data/marks.csv", this.subjectCode, marksType, allEditedMarks);
            JOptionPane.showMessageDialog(this, "Marks saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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