package model;

public class Mark {
    private String subject;
    private String username;
    private int quiz;
    private int assignment;
    private int mid;
    private int finalExam;

    public Mark(String subject, String username, int quiz, int assignment, int mid, int finalExam) {
        this.subject = subject;
        this.username = username;
        this.quiz = quiz;
        this.assignment = assignment;
        this.mid = mid;
        this.finalExam = finalExam;
    }

    // Getters
    public String getSubject() { return subject; }
    public String getUsername() { return username; }
    public int getQuiz() { return quiz; }
    public int getAssignment() { return assignment; }
    public int getMid() { return mid; }
    public int getFinalExam() { return finalExam; }

    // Setters
    public void setQuiz(int quiz) { this.quiz = quiz; }
    public void setAssignment(int assignment) { this.assignment = assignment; }
    public void setMid(int mid) { this.mid = mid; }
    public void setFinalExam(int finalExam) { this.finalExam = finalExam; }

    // Method to convert the object to a CSV-formatted string for file writing
    public String toCsvString() {
        return subject + "," + username + "," + quiz + "," + assignment + "," + mid + "," + finalExam;
    }
}