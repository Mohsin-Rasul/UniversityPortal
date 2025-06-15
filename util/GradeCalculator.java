package util;

import java.util.ArrayList;
import java.util.List;

public class GradeCalculator {

    public static String calculateAbsolute(double totalScore) {
        if (totalScore >= 85) return "A";
        if (totalScore >= 75) return "B";
        if (totalScore >= 60) return "C";
        if (totalScore >= 50) return "D";
        return "F";
    }

    public static String calculateRelative(double studentTotal, ArrayList<Double> allScores) {
        if (allScores == null || allScores.size() < 2) {
            return calculateAbsolute(studentTotal);
        }

        double sum = 0;
        for (double score : allScores) {
            sum += score;
        }
        double mean = sum / allScores.size();

        double standardDeviation = 0;
        for (double score : allScores) {
            standardDeviation += Math.pow(score - mean, 2);
        }
        double stdDev = Math.sqrt(standardDeviation / allScores.size());

        if (studentTotal > mean + 1.5 * stdDev) return "A";
        if (studentTotal > mean + 0.5 * stdDev) return "B";
        if (studentTotal > mean - 0.5 * stdDev) return "C";
        if (studentTotal > mean - 1.5 * stdDev) return "D";
        return "F";
    }
}