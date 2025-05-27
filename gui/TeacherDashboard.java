package gui;

import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TeacherDashboard extends JFrame {
    public TeacherDashboard() {
        setTitle("Teacher Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        JLabel title = new JLabel("Enter Student Marks");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        // Input Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        formPanel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField subject = new JTextField();
        JTextField student = new JTextField();
        JTextField quiz = new JTextField();
        JTextField assignment = new JTextField();
        JTextField mid = new JTextField();
        JTextField fin = new JTextField();

        formPanel.add(new JLabel("Subject:")); formPanel.add(subject);
        formPanel.add(new JLabel("Student Username:")); formPanel.add(student);
        formPanel.add(new JLabel("Quiz Marks:")); formPanel.add(quiz);
        formPanel.add(new JLabel("Assignment Marks:")); formPanel.add(assignment);
        formPanel.add(new JLabel("Mid Marks:")); formPanel.add(mid);
        formPanel.add(new JLabel("Final Marks:")); formPanel.add(fin);

        // Save Button
        JButton save = new JButton("Save Marks");
        save.setFont(new Font("Segoe UI", Font.BOLD, 14));
        save.setBackground(new Color(59, 89, 182));
        save.setForeground(Color.WHITE);
        save.setFocusPainted(false);
        save.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Action listener
        save.addActionListener(e -> {
            try {
                // Validate empty fields
                if (subject.getText().isEmpty() || student.getText().isEmpty()
                        || quiz.getText().isEmpty() || assignment.getText().isEmpty()
                        || mid.getText().isEmpty() || fin.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                CSVManager.saveMarks("data/marks.csv", subject.getText(), student.getText(),
                        Integer.parseInt(quiz.getText()),
                        Integer.parseInt(assignment.getText()),
                        Integer.parseInt(mid.getText()),
                        Integer.parseInt(fin.getText()));
                JOptionPane.showMessageDialog(this, "Marks saved successfully!");
                // Clear fields
                subject.setText("");
                student.setText("");
                quiz.setText("");
                assignment.setText("");
                mid.setText("");
                fin.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter only numbers for marks.");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving marks.");
            }
        });

        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(save);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}
