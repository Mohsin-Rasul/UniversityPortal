package util;

import model.User;
import java.io.*;
import java.util.*;

public class CSVManager {
    public static List<User> loadUsers(String filePath) throws IOException {
        List<User> users = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3)
                users.add(new User(parts[0], parts[1], parts[2]));
        }
        br.close();
        return users;
    }

    public static void saveMarks(String filePath, String subject, String student, int quiz, int assignment, int mid, int fin) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(subject + "," + student + "," + quiz + "," + assignment + "," + mid + "," + fin + "\n");
        bw.close();
    }

    public static List<String[]> loadMarks(String filePath) throws IOException {
        List<String[]> marks = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null)
            marks.add(line.split(","));
        br.close();
        return marks;
    }
}
