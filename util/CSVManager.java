package util;

import model.User;
import model.Mark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /**
     * Corrected batch update logic.
     * @param filepath Path to marks.csv
     * @param subject The subject/class identifier (e.g., "CY1121") for which marks are being updated.
     * @param type The type of mark being updated (e.g., "quiz", "assignment").
     * @param marksToUpdate A map of usernames to their new mark values.
     * @throws IOException
     */
    public static void batchUpdateMarks(String filepath, String subject, String type, Map<String, Integer> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);

        for (Map.Entry<String, Integer> entry : marksToUpdate.entrySet()) {
            String username = entry.getKey();
            int newMarkValue = entry.getValue();

            // Find an existing mark for this specific student AND subject
            Optional<Mark> existingMarkOpt = allMarks.stream()
                .filter(m -> m.getUsername().equals(username) && m.getSubject().equals(subject))
                .findFirst();
            
            Mark markToUpdate;
            if (existingMarkOpt.isPresent()) {
                // If a record exists for this subject, use it.
                markToUpdate = existingMarkOpt.get();
            } else {
                // **FIX APPLIED HERE**: If no record exists for this subject, create a new one
                // using the provided subject identifier instead of "DefaultSubject".
                markToUpdate = new Mark(subject, username, 0, 0, 0, 0);
                allMarks.add(markToUpdate); // Add the new record to the list to be saved.
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