package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceManager {

    private static final String ATTENDANCE_FILE = "attendance/data/attendance.csv";

    public static List<String[]> getAttendanceRecords() {
        List<String[]> records = new ArrayList<>();
        File file = new File(ATTENDANCE_FILE);

        if (!file.exists()) {
            System.err.println("Attendance file not found: " + ATTENDANCE_FILE);
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    System.out.println("Read record: " + parts[0] + ", " + parts[1]);
                    records.add(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }
}
