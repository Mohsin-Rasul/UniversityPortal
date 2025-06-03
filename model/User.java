package model;

public class User {
    private String username;
    private String password;
    private String role;
    private String regNo;

    public User(String username, String password, String role, String regNo) {
        // Trim whitespace from string inputs to ensure clean data for comparisons.
        this.username = username.trim();
        this.password = password.trim(); // Trimming password as well
        this.role = role.trim();
        this.regNo = regNo.trim();
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getRegNo() { return regNo; }
}