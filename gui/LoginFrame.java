package gui;

import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField loginIdField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("University Portal Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 270); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Login to Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Label for Login ID, which can be username for teacher or RegNo for student
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Login ID (Username/RegNo):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; loginIdField = new JTextField(15); panel.add(loginIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passwordField = new JPasswordField(15); panel.add(passwordField, gbc);
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        loginButton = new JButton("Login");
        panel.add(loginButton);
        return panel;
    }

    private void performLogin() {
        try {
            List<User> users = CSVManager.loadUsers("data/users.csv");
            String loginInput = loginIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            User matchedUser = null;

            for (User user : users) {
                boolean idMatch = false;
                // A student logs in with their Registration Number
                if ("student".equalsIgnoreCase(user.getRole())) {
                    if (user.getRegNo().equalsIgnoreCase(loginInput)) {
                        idMatch = true;
                    }
                // A teacher logs in with their username
                } else if ("teacher".equalsIgnoreCase(user.getRole())) {
                    if (user.getUsername().equalsIgnoreCase(loginInput)) {
                        idMatch = true;
                    }
                }

                if (idMatch && user.getPassword().equals(password)) {
                    matchedUser = user;
                    break;
                }
            }

            if (matchedUser != null) {
                dispose(); // Close the login window
                if ("teacher".equalsIgnoreCase(matchedUser.getRole())) {
                    // *** FIX APPLIED HERE ***
                    // Pass the teacher's username to the TeacherDashboard constructor.
                    new TeacherDashboard(matchedUser.getUsername());
                } else {
                    new StudentDashboard(matchedUser.getUsername());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}