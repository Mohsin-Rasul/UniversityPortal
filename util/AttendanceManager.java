package util;

import java.io.*;
import java.util.*;

public class AttendanceManager {
    private static final String FILE_PATH = "attendance/data/attendance.csv";

    public static List<String[]> getAttendanceRecords() {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}