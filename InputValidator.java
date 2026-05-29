package utils;

public class InputValidator {
    public static boolean isPositiveInt(String s) {
        try { return Integer.parseInt(s) > 0; } catch (Exception e) { return false; }
    }

    public static boolean isPositiveDouble(String s) {
        try { return Double.parseDouble(s) > 0.0; } catch (Exception e) { return false; }
    }
}
