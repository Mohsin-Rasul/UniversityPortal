package model;

import java.util.Arrays;

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
        Arrays.fill(this.quizzes, 0);
        Arrays.fill(this.assignments, 0);
    }

    // --- Getters and Setters ---
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

    /**
     * MODIFIED: Replaced Arrays.stream().sum() with a for-each loop.
     */
    public int getTotalQuizScore() {
        int sum = 0;
        for (int score : quizzes) {
            sum += score;
        }
        return sum;
    }

    /**
     * MODIFIED: Replaced Arrays.stream().sum() with a for-each loop.
     */
    public int getTotalAssignmentScore() {
        int sum = 0;
        for (int score : assignments) {
            sum += score;
        }
        return sum;
    }

    /**
     * MODIFIED: Replaced the Stream API with a standard for-loop builder.
     */
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(subject).append(",").append(username).append(",");

        for (int i = 0; i < quizzes.length; i++) {
            sb.append(quizzes[i]);
            if (i < quizzes.length - 1) {
                sb.append(",");
            }
        }
        sb.append(",");

        for (int i = 0; i < assignments.length; i++) {
            sb.append(assignments[i]);
            if (i < assignments.length - 1) {
                sb.append(",");
            }
        }
        sb.append(",");

        sb.append(mid).append(",").append(finalExam);
        
        return sb.toString();
    }
}