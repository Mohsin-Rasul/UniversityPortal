package util;

import model.User;
import model.Mark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This class uses file handling techniques from Lab 12 and ArrayLists/Generics from Lab 10.
public class CSVManager {

    private static final String[] USERS_HEADER = {"username", "password", "role", "regNo"};
    private static final String[] MARKS_HEADER = {"Subject", "Username", "Quiz", "Assignment", "Mid", "Final"};

    // Generic method to read raw data from a CSV, skipping the header.
    private static List<String[]> readCsvData(String filepath) throws IOException {
        List<String[]> records = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return records; // Return empty list if no file

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // Skip header row
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line.split(","));
            }
        }
        return records;
    }
    
    // Generic method to write raw data to a CSV with a header.
    private static void writeCsvData(String filepath, String[] header, List<String[]> data) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.println(String.join(",", header));
            for (String[] row : data) {
                writer.println(String.join(",", row));
            }
        }
    }

    // Loads users using concepts from Lab 12 (File Handling) and Lab 10 (ArrayList).
    public static List<User> loadUsers(String filepath) throws IOException {
        List<User> users = new ArrayList<>();
        List<String[]> rawData = readCsvData(filepath);
        for (String[] parts : rawData) {
            if (parts.length >= 4) {
                users.add(new User(parts[0], parts[1], parts[2], parts[3]));
            }
        }
        return users;
    }

    // Loads marks into a list of Mark objects, using Generics (Lab 10).
    public static List<Mark> loadMarks(String filepath) throws IOException {
        List<Mark> marks = new ArrayList<>();
        List<String[]> rawData = readCsvData(filepath);
        for (String[] row : rawData) {
            if (row.length >= 6) {
                marks.add(new Mark(row[0], row[1], Integer.parseInt(row[2]), Integer.parseInt(row[3]), Integer.parseInt(row[4]), Integer.parseInt(row[5])));
            }
        }
        return marks;
    }
    
    // EFFICIENT BATCH UPDATE: Replaces the old, inefficient single-update method.
    // This reads the file once, updates all necessary records in memory, and writes back once.
    public static void batchUpdateMarks(String filepath, String type, Map<String, Integer> marksToUpdate) throws IOException {
        List<Mark> allMarks = loadMarks(filepath);
        Map<String, Mark> marksMap = new java.util.HashMap<>();
        for(Mark m : allMarks) {
            marksMap.put(m.getUsername(), m);
        }

        for (Map.Entry<String, Integer> entry : marksToUpdate.entrySet()) {
            String username = entry.getKey();
            int newMark = entry.getValue();
            
            Mark markToUpdate = marksMap.get(username);
            if (markToUpdate != null) {
                 switch (type.toLowerCase()) {
                    case "quiz": markToUpdate.setQuiz(newMark); break;
                    case "assignment": markToUpdate.setAssignment(newMark); break;
                    case "mid": markToUpdate.setMid(newMark); break;
                    case "final": markToUpdate.setFinalExam(newMark); break;
                }
            }
        }
        
        // Convert back to List<String[]> for writing
        List<String[]> dataToWrite = new ArrayList<>();
        for (Mark m : allMarks) {
            dataToWrite.add(m.toCsvString().split(","));
        }

        writeCsvData(filepath, MARKS_HEADER, dataToWrite);
    }
}