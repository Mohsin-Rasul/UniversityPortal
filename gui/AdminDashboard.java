package gui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Administrator Portal");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Admin Control Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton manageUsersBtn = createAdminButton("Manage Users (Students & Teachers)");
        JButton manageSubjectsBtn = createAdminButton("Manage Subjects & Courses");
        JButton manageEnrollmentBtn = createAdminButton("Manage Student Enrollments");

        mainPanel.add(manageUsersBtn);
        mainPanel.add(manageSubjectsBtn);
        mainPanel.add(manageEnrollmentBtn);
        add(mainPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
        
        // Action Listeners to open the management frames
        manageUsersBtn.addActionListener(e -> new UserManagementFrame());
        manageSubjectsBtn.addActionListener(e -> new SubjectManagementFrame());
        manageEnrollmentBtn.addActionListener(e -> new EnrollmentManagementFrame());
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    private JButton createAdminButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setFocusPainted(false);
        return button;
    }
}