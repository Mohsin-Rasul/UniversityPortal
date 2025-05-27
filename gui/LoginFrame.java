package gui;

import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        loginButton.addActionListener(e -> performLogin());

        add(panel);
        setVisible(true);
    }

    private void performLogin() {
        try {
            List<User> users = CSVManager.loadUsers("data/users.csv");
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(this, "Login Successful as " + user.getRole());
                    dispose();
                    if (user.getRole().equals("teacher")) new TeacherDashboard();
                    else new StudentDashboard(username);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading users.csv");
        }
    }
}
