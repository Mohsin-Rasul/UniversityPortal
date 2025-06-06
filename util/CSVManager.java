package util;

import model.User;
import model.Mark;
import model.MarkUpdate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {

    // Updated Header for the new file format
    private static final String[] MARKS_HEADER = {"Subject", "Username", "Quiz1", "Quiz2", "Quiz3", "Quiz4", "Assign1", "Assign2", "Assign3", "Assign4", "Mid", "Final"};

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

    // Correctly loads the new marks format with 4 quizzes and 4 assignments
    public static List<Mark> loadMarks(String filepath) throws IOException {
        List<Mark> marks = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) {
            // If file doesn't exist, create it with the correct new header
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
                if (row.length >= 12) { // Subject, User + 4 Quizzes + 4 Assignments + Mid + Final
                    int[] quizzes = new int[4];
                    int[] assignments = new int[4];
                    for (int i = 0; i < 4; i++) {
                        quizzes[i] = Integer.parseInt(row[2 + i]);
                        assignments[i] = Integer.parseInt(row[6 + i]);
                    }
                    int mid = Integer.parseInt(row[10]);
                    int finalExam = Integer.parseInt(row[11]);
                    // This now calls the correct constructor that takes arrays
                    marks.add(new Mark(row[0], row[1], quizzes, assignments, mid, finalExam));
                }
            }
        }
        return marks;
    }

    // Correctly updates specific quiz/assignment numbers
    public static void batchUpdateMarks(String filepath, String type, List<MarkUpdate> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);

        for (MarkUpdate update : marksToUpdate) {
            Mark studentMark = null;
            // Find the student's existing record
            for (Mark existingMark : allMarks) {
                if (existingMark.getUsername().equals(update.getUsername())) {
                    studentMark = existingMark;
                    break;
                }
            }

            // If the student has no marks record at all, create a new one
            if (studentMark == null) {
                studentMark = new Mark("DefaultSubject", update.getUsername());
                allMarks.add(studentMark);
            }
            
            // Update the correct mark based on the type (e.g., "quiz1", "assignment3")
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

        // Write all the updated records back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", MARKS_HEADER));
            for (Mark mark : allMarks) {
                writer.println(mark.toCsvString());
            }
        }
    }
}