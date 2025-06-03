package model;

public class User {
    private String username;
    private String password;
    private String role;
    private String regNo;

    public User(String username, String password, String role, String regNo) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.regNo = regNo;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getRegNo() { return regNo; }
}