package model;

public class Mark {
    private String subject;
    private String username;

    // MODIFIED: Replaced arrays with individual fields for each assessment.
    private int quiz1, quiz2, quiz3, quiz4;
    private int assignment1, assignment2, assignment3, assignment4;
    private int mid;
    private int finalExam;

    // MODIFIED: Main constructor now accepts individual marks instead of arrays.
    public Mark(String subject, String username, int q1, int q2, int q3, int q4, int a1, int a2, int a3, int a4, int mid, int finalExam) {
        this.subject = subject;
        this.username = username;
        this.quiz1 = q1;
        this.quiz2 = q2;
        this.quiz3 = q3;
        this.quiz4 = q4;
        this.assignment1 = a1;
        this.assignment2 = a2;
        this.assignment3 = a3;
        this.assignment4 = a4;
        this.mid = mid;
        this.finalExam = finalExam;
    }

    // MODIFIED: Simplified constructor initializes all marks to 0.
    public Mark(String subject, String username) {
        this.subject = subject;
        this.username = username;
        this.quiz1 = 0;
        this.quiz2 = 0;
        this.quiz3 = 0;
        this.quiz4 = 0;
        this.assignment1 = 0;
        this.assignment2 = 0;
        this.assignment3 = 0;
        this.assignment4 = 0;
        this.mid = 0;
        this.finalExam = 0;
    }

    // --- Getters ---
    public String getSubject() { return subject; }
    public String getUsername() { return username; }
    public int getMid() { return mid; }
    public int getFinalExam() { return finalExam; }
    public int getQuiz1() { return quiz1; }
    public int getQuiz2() { return quiz2; }
    public int getQuiz3() { return quiz3; }
    public int getQuiz4() { return quiz4; }
    public int getAssignment1() { return assignment1; }
    public int getAssignment2() { return assignment2; }
    public int getAssignment3() { return assignment3; }
    public int getAssignment4() { return assignment4; }

    // --- Setters ---
    public void setMid(int mid) { this.mid = mid; }
    public void setFinalExam(int finalExam) { this.finalExam = finalExam; }
    public void setQuiz1(int quiz1) { this.quiz1 = quiz1; }
    public void setQuiz2(int quiz2) { this.quiz2 = quiz2; }
    public void setQuiz3(int quiz3) { this.quiz3 = quiz3; }
    public void setQuiz4(int quiz4) { this.quiz4 = quiz4; }
    public void setAssignment1(int assignment1) { this.assignment1 = assignment1; }
    public void setAssignment2(int assignment2) { this.assignment2 = assignment2; }
    public void setAssignment3(int assignment3) { this.assignment3 = assignment3; }
    public void setAssignment4(int assignment4) { this.assignment4 = assignment4; }


    // MODIFIED: Sums the individual quiz fields.
    public int getTotalQuizScore() {
        return quiz1 + quiz2 + quiz3 + quiz4;
    }

    // MODIFIED: Sums the individual assignment fields.
    public int getTotalAssignmentScore() {
        return assignment1 + assignment2 + assignment3 + assignment4;
    }
    
    // MODIFIED: Builds the CSV string from individual fields.
    public String toCsvString() {
        return String.join(",",
            subject,
            username,
            String.valueOf(quiz1),
            String.valueOf(quiz2),
            String.valueOf(quiz3),
            String.valueOf(quiz4),
            String.valueOf(assignment1),
            String.valueOf(assignment2),
            String.valueOf(assignment3),
            String.valueOf(assignment4),
            String.valueOf(mid),
            String.valueOf(finalExam)
        );
    }
}