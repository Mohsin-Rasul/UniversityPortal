package gui;

import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarksEntryFrame extends JFrame {
    private static final int STUDENTS_PER_PAGE = 10;

    private List<JTextField> markFields = new ArrayList<>();
    private List<User> students = new ArrayList<>();
    private JPanel listPanel;
    private int currentPage = 0;
    private String type;

    private JButton prevBtn;
    private JButton nextBtn;

    public MarksEntryFrame(String type) {
        this.type = type;

        setTitle("Enter " + capitalize(type) + " Marks");
        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(createHeaderLabel("Reg No"));
        headerPanel.add(createHeaderLabel("Username"));
        headerPanel.add(createHeaderLabel(capitalize(type) + " Marks"));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // List panel (where students are shown)
        listPanel = new JPanel(new GridLayout(STUDENTS_PER_PAGE, 3, 8, 6));
        listPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Pagination buttons
        prevBtn = new JButton("Previous");
        nextBtn = new JButton("Next");

        prevBtn.setEnabled(false); // Initially on first page

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateStudentList();
            }
        });

        nextBtn.addActionListener(e -> {
            if ((currentPage + 1) * STUDENTS_PER_PAGE < students.size()) {
                currentPage++;
                updateStudentList();
            }
        });

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.add(prevBtn);
        paginationPanel.add(nextBtn);
        mainPanel.add(paginationPanel, BorderLayout.NORTH);

        // Save button
        JButton saveButton = new JButton("Save All Marks");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        saveButton.setBackground(new Color(45, 118, 232));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(30, 90, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(45, 118, 232));
            }
        });

        saveButton.addActionListener(e -> {
            try {
                for (int i = 0; i < students.size(); i++) {
                    JTextField markField = markFields.get(i);
                    String markText = markField.getText().trim();
                    if (!markText.isEmpty()) {
                        int mark = Integer.parseInt(markText);
                        CSVManager.updateSingleMark("data/marks.csv", students.get(i).getUsername(), type, mark);
                    }
                }
                JOptionPane.showMessageDialog(this, "Marks saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric marks.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving marks.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Load students from file
        loadStudents();

        // Show first page
        updateStudentList();

        setVisible(true);
    }

    private void loadStudents() {
        try {
            List<User> allUsers = CSVManager.loadUsers("data/users.csv");
            for (User user : allUsers) {
                if ("student".equalsIgnoreCase(user.getRole())) {
                    students.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load students.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudentList() {
        markFields.clear();
        listPanel.removeAll();

        int start = currentPage * STUDENTS_PER_PAGE;
        int end = Math.min(start + STUDENTS_PER_PAGE, students.size());

        for (int i = start; i < end; i++) {
            User user = students.get(i);

            JLabel regLabel = new JLabel(user.getRegNo());
            regLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            regLabel.setForeground(new Color(50, 50, 50));

            JLabel userLabel = new JLabel(user.getUsername());
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            userLabel.setForeground(new Color(50, 50, 50));

            JTextField markField = new JTextField();
            markField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            markField.setPreferredSize(new Dimension(50, 24));
            markField.setMaximumSize(new Dimension(50, 24));
            markField.setMinimumSize(new Dimension(50, 24));
            markField.setHorizontalAlignment(JTextField.CENTER);
            markField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
            markFields.add(markField);

            listPanel.add(regLabel);
            listPanel.add(userLabel);
            listPanel.add(markField);
        }

        listPanel.revalidate();
        listPanel.repaint();

        // Update buttons enabled state
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled((currentPage + 1) * STUDENTS_PER_PAGE < students.size());
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(new Color(45, 118, 232));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
