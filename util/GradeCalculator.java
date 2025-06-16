package util;

import java.util.ArrayList;

public class GradeCalculator {

    public static String calculateAbsolute(double totalScore) {
        if (totalScore >= 86) return "A";
        if(totalScore >= 82) return "A-";
        if (totalScore >= 78) return "B+";
        if (totalScore >= 74) return "B";
        if (totalScore >= 70) return "B-";
        if (totalScore >= 66) return "C+";
        if (totalScore >= 62) return "C";
        if (totalScore >= 58) return "C-";
        if (totalScore >= 54) return "D+";
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
        if (studentTotal >= mean + 1.5 * stdDev) return "A";
        if (studentTotal >= mean + 1.2 * stdDev) return "A-";
        if (studentTotal >= mean + 0.9 * stdDev) return "B+";
        if (studentTotal >= mean + 0.6 * stdDev) return "B";
        if (studentTotal >= mean + 0.3 * stdDev) return "B-";
        if (studentTotal >= mean + 0.0 * stdDev) return "C+"; 
        if (studentTotal >= mean - 0.4 * stdDev) return "C";
        if (studentTotal >= mean - 0.8 * stdDev) return "C-";
        if (studentTotal >= mean - 1.2 * stdDev) return "D+";
        if (studentTotal >= mean - 1.6 * stdDev) return "D";

        return "F";
    }
}