package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AttendanceManager {

    private static final String ATTENDANCE_FILE = "data/attendance.csv";

    public static ArrayList<String[]> getAttendanceRecords() {
        ArrayList<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            br.readLine(); 
            while ((line = br.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Could not read attendance file: " + e.getMessage());
        }
        return records;
    }
}