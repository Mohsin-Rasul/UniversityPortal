package util;

public class GradeCalculator {
    public static String calculateAbsolute(int total) {
        if (total >= 85) return "A";
        if (total >= 75) return "B";
        if (total >= 60) return "C";
        if (total >= 50) return "D";
        return "F";
    }
}
