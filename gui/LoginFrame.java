package gui;

import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

// This GUI is improved using concepts from Lab 11 (BorderLayout, Panels, Borders).
public class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        setTitle("University Portal Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JLabel titleLabel = new JLabel("Login to Portal", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Input Fields Panel using a more structured layout
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; fieldsPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; usernameField = new JTextField(15); fieldsPanel.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; fieldsPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; passwordField = new JPasswordField(15); fieldsPanel.add(passwordField, gbc);

        add(fieldsPanel, BorderLayout.CENTER);

        // Login Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        loginButton = new JButton("Login");
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Handling (Lab 11)
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        setVisible(true);
    }

    private void performLogin() {
        // Logic remains the same as previous version, adhering to lab constraints.
        try {
            List<User> users = CSVManager.loadUsers("data/users.csv");
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean loggedIn = false;

            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    dispose();
                    if ("teacher".equalsIgnoreCase(user.getRole())) {
                        new TeacherDashboard();
                    } else {
                        new StudentDashboard(username);
                    }
                    loggedIn = true;
                    break;
                }
            }
            if (!loggedIn) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}