package gui;

import model.User;
import util.CSVManager;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class LoginFrame extends JFrame {
    private JTextField loginIdField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("University Portal Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 270);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Simplified component creation
        add(createHeader("Login to Portal"), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    private JPanel createHeader(String title) {
        JPanel panel = new JPanel();
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(titleLabel);
        return panel;
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Using a loop to create labels and fields would be over-engineering here
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Login ID (Username/RegNo):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; loginIdField = new JTextField(15); panel.add(loginIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passwordField = new JPasswordField(15); panel.add(passwordField, gbc);
        
        // Add action listener to password field to login on Enter key
        passwordField.addActionListener(e -> performLogin());

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> performLogin());
        panel.add(loginButton);
        return panel;
    }

    private void performLogin() {
        try {
            List<User> users = CSVManager.loadUsers("data/users.csv");
            String loginInput = loginIdField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Use stream to find matching user
            Optional<User> matchedUser = users.stream().filter(user -> 
                (user.getRole().equalsIgnoreCase("student") && user.getRegNo().equalsIgnoreCase(loginInput) ||
                 user.getRole().equalsIgnoreCase("teacher") && user.getUsername().equalsIgnoreCase(loginInput)) &&
                user.getPassword().equals(password)
            ).findFirst();

            if (matchedUser.isPresent()) {
                dispose(); // Close login window
                User user = matchedUser.get();
                if ("teacher".equalsIgnoreCase(user.getRole())) {
                    new TeacherDashboard(user.getUsername());
                } else {
                    new StudentDashboard(user.getUsername());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}