package keno.guildedparties.utils;

public class MathUtil {
    public static double normalizeValues(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    public static double normalizeValues(float value, float min, float max) {
        return normalizeValues((double) value, min, max);
    }

    public static double normalizeValues(int value, int min, int max) {
        return normalizeValues((double) value, min, max);
    }
}
