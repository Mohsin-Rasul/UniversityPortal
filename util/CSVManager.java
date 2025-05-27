package util;

import model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {

    // Load marks from CSV (expected format: Subject, Username, Quiz, Assignment, Mid, Final)
    public static List<String[]> loadMarks(String filepath) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        }
        return data;
    }

    // Save all marks back to CSV
    public static void saveAllMarks(String filepath, List<String[]> data) throws IOException {
        try (PrintWriter writer = new PrintWriter(filepath)) {
            for (String[] row : data) {
                writer.println(String.join(",", row));
            }
        }
    }

    // Update one mark of a given type for a username
    public static void updateSingleMark(String filepath, String username, String type, int mark) throws IOException {
        List<String[]> data = loadMarks(filepath);
        boolean updated = false;

        for (String[] row : data) {
            if (row.length > 1 && row[1].equals(username)) {
                switch (type.toLowerCase()) {
                    case "quiz": row[2] = String.valueOf(mark); break;
                    case "assignment": row[3] = String.valueOf(mark); break;
                    case "mid": row[4] = String.valueOf(mark); break;
                    case "final": row[5] = String.valueOf(mark); break;
                }
                updated = true;
                break;
            }
        }

        if (!updated) {
            // Add new record if username not found
            String[] newRow = new String[6];
            newRow[0] = "Subject"; // Adjust if you want to specify real subject
            newRow[1] = username;
            newRow[2] = type.equalsIgnoreCase("quiz") ? String.valueOf(mark) : "0";
            newRow[3] = type.equalsIgnoreCase("assignment") ? String.valueOf(mark) : "0";
            newRow[4] = type.equalsIgnoreCase("mid") ? String.valueOf(mark) : "0";
            newRow[5] = type.equalsIgnoreCase("final") ? String.valueOf(mark) : "0";
            data.add(newRow);
        }

        saveAllMarks(filepath, data);
    }

    // Load users from CSV (expected format: username,password,role,regNo)
    public static List<User> loadUsers(String filepath) throws IOException {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String username = parts[0];
                    String password = parts[1];
                    String role = parts[2];
                    String regNo = parts[3];
                    users.add(new User(username, password, role, regNo));
                }
            }
        }
        return users;
    }
}
