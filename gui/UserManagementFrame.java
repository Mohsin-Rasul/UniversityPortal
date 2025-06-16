import model.User;
import util.CSVManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class UserManagementFrame extends JFrame {
    private class UserTableModel extends AbstractTableModel {
        private final ArrayList<User> userList;
        private final String[] columnNames = {"Username", "Password", "Role", "Registration No."};
        public UserTableModel(ArrayList<User> userList) { this.userList = userList; }
        @Override public int getRowCount() { return userList.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int column) { return columnNames[column]; }
        @Override public Object getValueAt(int r, int c) {
            User u = userList.get(r);
            switch (c) {
                case 0: return u.getUsername();
                case 1: return u.getPassword();
                case 2: return u.getRole();
                case 3: return u.getRegNo();
                default: return null;
            }
        }
        public ArrayList<User> getUserList() { return userList; }
        public User getUserAt(int r) { return userList.get(r); }
        public void addUser(User u) { userList.add(u); fireTableRowsInserted(userList.size()-1, userList.size()-1); }
        public void removeUser(int r) { userList.remove(r); fireTableRowsDeleted(r, r); }
        public void updateUser(int r, User u) { userList.set(r, u); fireTableRowsUpdated(r, r); }
    }

    private JTable userTable;
    private UserTableModel tableModel;
    private final String usersFilePath = "data/users.csv";

    public UserManagementFrame() {
        setTitle("User Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ArrayList<User> userList;
        try {
            userList = CSVManager.loadUsers(usersFilePath);
        } catch (IOException e) {
            userList = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        tableModel = new UserTableModel(userList);
        userTable = new JTable(tableModel);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(userTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        setVisible(true);
    }
    
    private void saveUsersToFile() {
        try {
            CSVManager.saveUsers(usersFilePath, tableModel.getUserList());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"student", "teacher", "admin"});
        JTextField regNoField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);
        panel.add(new JLabel("Registration No. (if student):"));
        panel.add(regNoField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = (String) roleComboBox.getSelectedItem();
            String regNo = "student".equals(role) ? regNoField.getText().trim() : "N/A";
            if (!username.isEmpty() && !password.isEmpty()) {
                tableModel.addUser(new User(username, password, role, regNo));
                saveUsersToFile();
            } else {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            User userToEdit = tableModel.getUserAt(selectedRow);
            JTextField usernameField = new JTextField(userToEdit.getUsername());
            JTextField passwordField = new JTextField(userToEdit.getPassword());
            JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"student", "teacher", "admin"});
            roleComboBox.setSelectedItem(userToEdit.getRole());
            JTextField regNoField = new JTextField(userToEdit.getRegNo());
            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Role:"));
            panel.add(roleComboBox);
            panel.add(new JLabel("Registration No.:"));
            panel.add(regNoField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                User updatedUser = new User(
                    usernameField.getText().trim(),
                    passwordField.getText().trim(),
                    (String) roleComboBox.getSelectedItem(),
                    "student".equals(roleComboBox.getSelectedItem()) ? regNoField.getText().trim() : "N/A"
                );
                tableModel.updateUser(selectedRow, updatedUser);
                saveUsersToFile();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeUser(selectedRow);
                saveUsersToFile();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
}