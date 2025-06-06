package util;

import java.io.*;

public class ConfigManager {

    private static final String CONFIG_FILE_PATH = "data/grading_policy.txt";

    /**
     * Saves the chosen grading policy to a simple text file using FileWriter.
     * @param policy The policy to save ("absolute" or "relative").
     */
    public static void saveGradingPolicy(String policy) {
        // This uses FileWriter, which was covered in your lab.
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE_PATH))) {
            writer.println(policy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the grading policy from a simple text file using BufferedReader.
     * Defaults to "absolute" if the file doesn't exist or is empty.
     * @return The currently saved grading policy.
     */
    public static String loadGradingPolicy() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            return "absolute"; // Default if file doesn't exist
        }

        // This uses BufferedReader and FileReader, as covered in your lab.
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String policy = reader.readLine();
            // Check if the file had content, otherwise return default
            if (policy != null && !policy.trim().isEmpty()) {
                return policy.trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Default in case the file is empty or an error occurs
        return "absolute"; 
    }
}