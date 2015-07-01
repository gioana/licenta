package com.diploma.android.iruntracking.utils;

public abstract class Calculator {
    private static final double CONSTANT_A = 0.0395;
    private static final double CONSTANT_B = 0.00327;
    private static final double CONSTANT_C = 0.000455;
    private static final double CONSTANT_K = 1;

    public static long getCaloriesPerMinute(float weightKg, float paceMinPerKm, long durationSeconds) {
        if (paceMinPerKm == 0 || durationSeconds == 0) {
            return 0;
        }
        double weightLbs = weightKg * 2.2;
        double speedMph = 37.28 / paceMinPerKm;

        return (long)(getCaloriesPerPound(weightLbs, speedMph) * weightLbs);
    }

    private static double getCaloriesPerPound(double weightLbs, double speedMph) {
        double constantD = 0.00801 * Math.pow(weightLbs / 154, 0.425) / weightLbs;

        return CONSTANT_A + CONSTANT_B * speedMph + CONSTANT_C * Math.pow(speedMph, 2) +
                CONSTANT_K * constantD * Math.pow(speedMph, 3);
    }

    public static float getDistanceKm(float distanceM) {
        return distanceM / 1000;
    }

    public static float getPace(long durationSeconds, float distanceM) {
        if (durationSeconds == 0 || distanceM == 0) {
            return 0;
        }
        float durationMinutes = (float)durationSeconds / 60;
        float distanceKm = distanceM / 1000;

        return durationMinutes / distanceKm;
    }

    public static float getAvgPace(float sumPace, long durationSeconds) {
        return sumPace / durationSeconds;
    }
}
