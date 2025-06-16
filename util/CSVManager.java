package util;

import model.User;
import model.Mark;
import model.MarkUpdate;
import model.Subject;

import java.io.*;
import java.util.ArrayList;

public class CSVManager {

    private static final String USERS_HEADER = "username,password,role,regNo";
    private static final String MARKS_HEADER = "Subject,Username,Quiz1,Quiz2,Quiz3,Quiz4,Assign1,Assign2,Assign3,Assign4,Mid,Final";
    private static final String SUBJECTS_HEADER = "SubjectCode,SubjectName,TeacherUsername";

    public static ArrayList<Subject> loadSubjects(String filepath) throws IOException {
        ArrayList<Subject> subjects = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
                writer.println(SUBJECTS_HEADER);
            }
            return subjects;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    subjects.add(new Subject(parts[0], parts[1], parts[2]));
                } else if (parts.length == 2) {
                    subjects.add(new Subject(parts[0], parts[1], "N/A"));
                }
            }
        }
        return subjects;
    }

    public static void saveSubjects(String filepath, ArrayList<Subject> subjects) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(SUBJECTS_HEADER);
            for (Subject subject : subjects) {
                String line = String.join(",", subject.getCode(), subject.getName(), subject.getTeacherUsername());
                writer.println(line);
            }
        }
    }

    public static ArrayList<User> loadUsers(String filepath) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        }
        return users;
    }
    
    public static void saveUsers(String filepath, ArrayList<User> users) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(USERS_HEADER);
            for (User user : users) {
                String line = String.join(",", user.getUsername(), user.getPassword(), user.getRole(), user.getRegNo());
                writer.println(line);
            }
        }
    }
    public static void enrollStudents(String filepath, String subjectCode, ArrayList<String> usernames) throws IOException {
        ArrayList<Mark> allMarks = loadMarks(filepath);

        ArrayList<String> alreadyEnrolled = new ArrayList<>();
        for (Mark mark : allMarks) {
            if (mark.getSubject().equalsIgnoreCase(subjectCode)) {
                alreadyEnrolled.add(mark.getUsername());
            }
        }

        for (String username : usernames) {
            if (!alreadyEnrolled.contains(username)) {
                allMarks.add(new Mark(subjectCode, username));
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", MARKS_HEADER));
            for (Mark mark : allMarks) {
                writer.println(mark.toCsvString());
            }
        }
    }
    public static ArrayList<Mark> loadMarks(String filepath) throws IOException {
        ArrayList<Mark> marks = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
                writer.println(String.join(",", MARKS_HEADER));
            }
            return marks;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                 String[] row = line.split(",");
                try {
                    if (row.length >= 12) {
                        // MODIFIED: Use a loop to parse all integer marks into a temporary array.
                        int[] scores = new int[10];
                        for (int i = 0; i < 10; i++) {
                            // row[i + 2] corresponds to Quiz1, Quiz2, ..., Mid, Final
                            scores[i] = Integer.parseInt(row[i + 2].trim());
                        }

                        // Create the Mark object using the parsed scores.
                        marks.add(new Mark(
                            row[0].trim(), row[1].trim(),
                            scores[0], scores[1], scores[2], scores[3], // Quizzes
                            scores[4], scores[5], scores[6], scores[7], // Assignments
                            scores[8], // Mid
                            scores[9]  // Final
                        ));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed row in marks.csv: " + line);
                }
            }
        }
        return marks;
    }

    public static void batchUpdateMarks(String filepath, String subjectCode, String type, ArrayList<MarkUpdate> marksToUpdate) throws IOException {
        ArrayList<Mark> allMarks = loadMarks(filepath);

        for (MarkUpdate update : marksToUpdate) {
            Mark studentMark = null;
            for (Mark existingMark : allMarks) {
                if (existingMark.getUsername().equals(update.getUsername()) && existingMark.getSubject().equalsIgnoreCase(subjectCode)) {
                    studentMark = existingMark;
                    break;
                }
            }
            if (studentMark == null) {
                studentMark = new Mark(subjectCode, update.getUsername());
                allMarks.add(studentMark);
            }
            
            String typeLower = type.toLowerCase();
            switch(typeLower) {
                case "quiz1": studentMark.setQuiz1(update.getMark()); break;
                case "quiz2": studentMark.setQuiz2(update.getMark()); break;
                case "quiz3": studentMark.setQuiz3(update.getMark()); break;
                case "quiz4": studentMark.setQuiz4(update.getMark()); break;
                case "assignment1": studentMark.setAssignment1(update.getMark()); break;
                case "assignment2": studentMark.setAssignment2(update.getMark()); break;
                case "assignment3": studentMark.setAssignment3(update.getMark()); break;
                case "assignment4": studentMark.setAssignment4(update.getMark()); break;
                case "mid": studentMark.setMid(update.getMark()); break;
                case "final": studentMark.setFinalExam(update.getMark()); break;
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", MARKS_HEADER));
            for (Mark mark : allMarks) {
                writer.println(mark.toCsvString());
            }
        }
    }
}