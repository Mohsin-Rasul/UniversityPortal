package model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Mark {
    private String subject;
    private String username;

    private int[] quizzes = new int[4];
    private int[] assignments = new int[4];
    private int mid;
    private int finalExam;

    public Mark(String subject, String username, int[] quizzes, int[] assignments, int mid, int finalExam) {
        this.subject = subject;
        this.username = username;
        this.quizzes = quizzes;
        this.assignments = assignments;
        this.mid = mid;
        this.finalExam = finalExam;
    }
    
    public Mark(String subject, String username) {
        this.subject = subject;
        this.username = username;
        // Initialize arrays with zeros
        Arrays.fill(this.quizzes, 0);
        Arrays.fill(this.assignments, 0);
    }

    public String getSubject() { return subject; }
    public String getUsername() { return username; }
    public int getMid() { return mid; }
    public int getFinalExam() { return finalExam; }
    public int[] getQuizzes() { return quizzes; }
    public int[] getAssignments() { return assignments; }

    public void setMid(int mid) { this.mid = mid; }
    public void setFinalExam(int finalExam) { this.finalExam = finalExam; }
    public void setQuizzes(int[] quizzes) { this.quizzes = quizzes; }
    public void setAssignments(int[] assignments) { this.assignments = assignments; }

    public int getTotalQuizScore() {
        return Arrays.stream(quizzes).sum();
    }

    public int getTotalAssignmentScore() {
        return Arrays.stream(assignments).sum();
    }

    public String toCsvString() {
        String quizzesStr = Arrays.stream(quizzes).mapToObj(String::valueOf).collect(Collectors.joining(","));
        String assignmentsStr = Arrays.stream(assignments).mapToObj(String::valueOf).collect(Collectors.joining(","));
        return subject + "," + username + "," + quizzesStr + "," + assignmentsStr + "," + mid + "," + finalExam;
    }
}