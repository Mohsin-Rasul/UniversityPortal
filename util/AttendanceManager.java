package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    private static final String ATTENDANCE_FILE = "data/attendance.csv"; 
    public static List<String[]> getAttendanceRecords() {
        List<String[]> records = new ArrayList<>();
        File file = new File(ATTENDANCE_FILE);

        if (!file.exists()) {
            System.err.println("Attendance file not found: " + ATTENDANCE_FILE);
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null) { 
                return records;
            }
            

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) { 
                    records.add(parts);
                } else if (parts.length == 2) {
                     records.add(new String[]{parts[0], parts[1], "N/A"});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}