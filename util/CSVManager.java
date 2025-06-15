package util;

import model.User;
import model.Mark;
import model.MarkUpdate;
import model.Subject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {

    private static final String USERS_HEADER = "username,password,role,regNo";
    private static final String MARKS_HEADER = "Subject,Username,Quiz1,Quiz2,Quiz3,Quiz4,Assign1,Assign2,Assign3,Assign4,Mid,Final";
    private static final String SUBJECTS_HEADER = "SubjectCode,SubjectName,TeacherUsername";

    public static List<Subject> loadSubjects(String filepath) throws IOException {
        List<Subject> subjects = new ArrayList<>();
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

    public static void saveSubjects(String filepath, List<Subject> subjects) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(SUBJECTS_HEADER);
            for (Subject subject : subjects) {
                String line = String.join(",", subject.getCode(), subject.getName(), subject.getTeacherUsername());
                writer.println(line);
            }
        }
    }

    public static List<User> loadUsers(String filepath) throws IOException {
        List<User> users = new ArrayList<>();
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
    
    public static void saveUsers(String filepath, List<User> users) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(USERS_HEADER);
            for (User user : users) {
                String line = String.join(",", user.getUsername(), user.getPassword(), user.getRole(), user.getRegNo());
                writer.println(line);
            }
        }
    }
    public static void enrollStudents(String filepath, String subjectCode, List<String> usernames) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);

        List<String> alreadyEnrolled = new ArrayList<>();
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
    public static List<Mark> loadMarks(String filepath) throws IOException {
        List<Mark> marks = new ArrayList<>();
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
                        int[] quizzes = new int[4];
                        int[] assignments = new int[4];
                        for (int i = 0; i < 4; i++) {
                            quizzes[i] = Integer.parseInt(row[2 + i].trim());
                            assignments[i] = Integer.parseInt(row[6 + i].trim());
                        }
                        int mid = Integer.parseInt(row[10].trim());
                        int finalExam = Integer.parseInt(row[11].trim());
                        marks.add(new Mark(row[0].trim(), row[1].trim(), quizzes, assignments, mid, finalExam));

                    } else if (row.length >= 6) { 
                        int[] quizzes = new int[]{Integer.parseInt(row[2].trim()), 0, 0, 0};
                        int[] assignments = new int[]{Integer.parseInt(row[3].trim()), 0, 0, 0};
                        int mid = Integer.parseInt(row[4].trim());
                        int finalExam = Integer.parseInt(row[5].trim());
                        marks.add(new Mark(row[0].trim(), row[1].trim(), quizzes, assignments, mid, finalExam));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Skipping malformed row in marks.csv: " + line);
                }
            }
        }
        return marks;
    }

    public static void batchUpdateMarks(String filepath, String subjectCode, String type, List<MarkUpdate> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);

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
            if (typeLower.startsWith("quiz")) {
                int quizNum = Integer.parseInt(typeLower.replace("quiz", "")) - 1;
                studentMark.getQuizzes()[quizNum] = update.getMark();
            } else if (typeLower.startsWith("assignment")) {
                int assignNum = Integer.parseInt(typeLower.replace("assignment", "")) - 1;
                studentMark.getAssignments()[assignNum] = update.getMark();
            } else if (typeLower.equals("mid")) {
                studentMark.setMid(update.getMark());
            } else if (typeLower.equals("final")) {
                studentMark.setFinalExam(update.getMark());
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