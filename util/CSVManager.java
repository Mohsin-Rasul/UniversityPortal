package util;

import model.User;
import model.Mark;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVManager {

    private static final String MARKS_HEADER = "Subject,Username,Quiz,Assignment,Mid,Final";

    // Simplified user loading with streams
    public static List<User> loadUsers(String filepath) throws IOException {
        try (var lines = Files.lines(Paths.get(filepath))) {
            return lines.skip(1) // Skip header
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length >= 4)
                        .map(p -> new User(p[0], p[1], p[2], p[3]))
                        .collect(Collectors.toList());
        }
    }

    // Simplified marks loading with streams
    public static List<Mark> loadMarks(String filepath) throws IOException {
        try (var lines = Files.lines(Paths.get(filepath))) {
            return lines.skip(1) // Skip header
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length >= 6)
                        .map(p -> new Mark(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5])))
                        .collect(Collectors.toList());
        }
    }

    // Simplified batch update logic
    public static void batchUpdateMarks(String filepath, String subject, String type, Map<String, Integer> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);
        Map<String, Mark> marksMap = allMarks.stream()
            .collect(Collectors.toMap(m -> m.getSubject() + "-" + m.getUsername(), m -> m));

        marksToUpdate.forEach((username, newMarkValue) -> {
            String key = subject + "-" + username;
            Mark mark = marksMap.computeIfAbsent(key, k -> {
                Mark newMark = new Mark(subject, username, 0, 0, 0, 0);
                allMarks.add(newMark); // Add to the main list
                return newMark;
            });

            switch (type.toLowerCase()) {
                case "quiz" -> mark.setQuiz(newMarkValue);
                case "assignment" -> mark.setAssignment(newMarkValue);
                case "mid" -> mark.setMid(newMarkValue);
                case "final" -> mark.setFinalExam(newMarkValue);
            }
        });

        // Write the updated list back to the file
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(MARKS_HEADER);
            allMarks.forEach(mark -> writer.println(mark.toCsvString()));
        }
    }
}