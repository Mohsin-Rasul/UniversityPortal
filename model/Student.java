package model;

public class Student {
    private String username;
    private String registrationNumber;

    public Student(String username, String registrationNumber) {
        this.username = username;
        this.registrationNumber = registrationNumber;
    }

    public String getUsername() { return username; }
    public String getRegistrationNumber() { return registrationNumber; }
}
