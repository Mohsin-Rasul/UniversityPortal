import model.User;
import util.CSVManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

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
        JPanel wrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 5, 10));
        
        loginIdField = new JTextField(15);
        passwordField = new JPasswordField(15);
        
        gridPanel.add(new JLabel("Login ID:"));
        gridPanel.add(loginIdField);
        gridPanel.add(new JLabel("Password:"));
        gridPanel.add(passwordField);
        
        wrapperPanel.add(gridPanel);
        return wrapperPanel;
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
            User foundUser = findUser(loginIdField.getText().trim(), new String(passwordField.getPassword()));
            if (foundUser != null) {
                dispose();
                String role = foundUser.getRole();
                if ("admin".equalsIgnoreCase(role)) {
                    new AdminDashboard();
                } else if ("teacher".equalsIgnoreCase(role)) {
                    new TeacherDashboard(foundUser.getUsername());
                } else {
                    new StudentDashboard(foundUser.getUsername());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login ID or Password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private User findUser(String loginInput, String password) throws IOException {
        List<User> users = CSVManager.loadUsers("data/users.csv");
        for (User user : users) {
            boolean idMatch = false;
            String role = user.getRole();
            if ("student".equalsIgnoreCase(role)) {
                if (user.getRegNo().equalsIgnoreCase(loginInput)) {
                    idMatch = true;
                }
            } else if ("teacher".equalsIgnoreCase(role) || "admin".equalsIgnoreCase(role)) {
                if (user.getUsername().equalsIgnoreCase(loginInput)) {
                    idMatch = true;
                }
            }
            if (idMatch && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}