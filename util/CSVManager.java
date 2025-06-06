package util;

import model.User;
import model.Mark;
import model.MarkUpdate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
// import java.util.Map; // This import is no longer needed
// import java.util.HashMap; // This import is no longer needed

public class CSVManager {

    private static final String[] MARKS_HEADER = {"Subject", "Username", "Quiz", "Assignment", "Mid", "Final"};

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

    public static List<Mark> loadMarks(String filepath) throws IOException {
        List<Mark> marks = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return marks;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); 
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length >= 6) {
                    marks.add(new Mark(row[0], row[1], Integer.parseInt(row[2]), Integer.parseInt(row[3]), Integer.parseInt(row[4]), Integer.parseInt(row[5])));
                }
            }
        }
        return marks;
    }

    public static void batchUpdateMarks(String filepath, String type, List<MarkUpdate> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);

        // For each updated mark from the GUI...
        for (MarkUpdate update : marksToUpdate) {
            boolean studentFound = false;
            // ...loop through the existing marks to find a match.
            for (Mark existingMark : allMarks) {
                if (existingMark.getUsername().equals(update.getUsername())) {
                    // Found the student, now update the correct mark type.
                    switch (type.toLowerCase()) {
                        case "quiz": existingMark.setQuiz(update.getMark()); break;
                        case "assignment": existingMark.setAssignment(update.getMark()); break;
                        case "mid": existingMark.setMid(update.getMark()); break;
                        case "final": existingMark.setFinalExam(update.getMark()); break;
                    }
                    studentFound = true;
                    break; 
                }
            }
            
            if (!studentFound) {
                // If the student wasn't in the marks file, create a new record for them.
                Mark newMarkRecord = new Mark("DefaultSubject", update.getUsername(), 0, 0, 0, 0);
                switch (type.toLowerCase()) {
                    case "quiz": newMarkRecord.setQuiz(update.getMark()); break;
                    case "assignment": newMarkRecord.setAssignment(update.getMark()); break;
                    case "mid": newMarkRecord.setMid(update.getMark()); break;
                    case "final": newMarkRecord.setFinalExam(update.getMark()); break;
                }
                allMarks.add(newMarkRecord);
            }
        }

        // Write the entire updated list back to the file.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", MARKS_HEADER));
            for (Mark mark : allMarks) {
                writer.println(mark.toCsvString());
            }
        }
    }
}