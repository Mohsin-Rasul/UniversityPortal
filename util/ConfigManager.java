package util;

import java.io.*;

public class ConfigManager {

    private static final String CONFIG_FILE_PATH = "data/grading_policy.txt";
    public static void saveGradingPolicy(String policy) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE_PATH))) {
            writer.println(policy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadGradingPolicy() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            return "absolute"; 
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String policy = reader.readLine();
            if (policy != null && !policy.trim().isEmpty()) {
                return policy.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "absolute"; 
    }
}