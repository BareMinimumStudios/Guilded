package keno.guildedparties.utils;

public class MathUtil {
    public static double denormalizeValues(double normalizedValue, double min, double max) {
        return normalizedValue * (max - min) - min;
    }

    public static double denormalizeValues(float normalizedValue, float min, float max) {
        return denormalizeValues((double) normalizedValue, min, max);
    }

    public static double denormalizeValues(int normalizedValue, int min, int max) {
        return denormalizeValues((double) normalizedValue, min, max);
    }

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
