package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    private static final String ATTENDANCE_FILE = "data/attendance.csv"; // Ensure path is correct

    public static List<String[]> getAttendanceRecords() {
        List<String[]> records = new ArrayList<>();
        File file = new File(ATTENDANCE_FILE);

        if (!file.exists()) {
            System.err.println("Attendance file not found: " + ATTENDANCE_FILE);
            return records; // Return empty list
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // Read header
            if (line == null) { // Empty file
                return records;
            }
            
            // Optionally verify header: e.g., if (!"Username,Timestamp,Section".equals(line)) { ... }

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                // Now expecting 3 parts: Username, Timestamp, Section
                if (parts.length >= 3) { 
                    records.add(parts);
                } else if (parts.length == 2) {
                    // Handle old format if necessary, or log a warning
                    // For now, we'll add it with a null/empty section for backward compatibility display
                     records.add(new String[]{parts[0], parts[1], "N/A"});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}