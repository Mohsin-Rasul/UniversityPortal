package util;

import model.User;
import model.Mark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static void batchUpdateMarks(String filepath, String type, Map<String, Integer> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);
        Map<String, Mark> marksMap = allMarks.stream()
                                              .collect(Collectors.toMap(Mark::getUsername, m -> m, (a, b) -> a));

        for (Map.Entry<String, Integer> entry : marksToUpdate.entrySet()) {
            String username = entry.getKey();
            int newMarkValue = entry.getValue();

            Mark markToUpdate = marksMap.get(username);

            // ** BUG FIX **
            // If the student has no existing record in marks.csv, create a new one.
            if (markToUpdate == null) {
                markToUpdate = new Mark("DefaultSubject", username, 0, 0, 0, 0);
                allMarks.add(markToUpdate); // Add the new record to the list that will be saved.
            }
            
            // Now, update the correct mark on either the existing or the new object.
            switch (type.toLowerCase()) {
                case "quiz": markToUpdate.setQuiz(newMarkValue); break;
                case "assignment": markToUpdate.setAssignment(newMarkValue); break;
                case "mid": markToUpdate.setMid(newMarkValue); break;
                case "final": markToUpdate.setFinalExam(newMarkValue); break;
            }
        }

        // Write the updated list back to the file.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", MARKS_HEADER));
            for (Mark mark : allMarks) {
                writer.println(mark.toCsvString());
            }
        }
    }
}