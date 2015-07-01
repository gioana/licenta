package com.diploma.android.iruntracking.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Formatter extends Format {

    public static String formatDuration(long durationSeconds) {
        long seconds = durationSeconds % 60;
        long minutes = ((durationSeconds - seconds) / 60) % 60;
        long hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
        if (hours == 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatDistance(float distanceM) {
        return String.format("%.2f", Calculator.getDistanceKm(distanceM));
    }

    public static String formatPace(float paceMinPerKm) {
        long hours = 0;
        long minutes = (long) paceMinPerKm % 60;
        if (paceMinPerKm > 60) {
            hours = (long)(paceMinPerKm - minutes) / 60;
        }
        long seconds = (long)(paceMinPerKm - minutes - (hours * 60)) * 60;

        if (hours == 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String timeOfDay = "";

        if(hourOfDay >= 0 && hourOfDay < 12){
            timeOfDay = "Morning run";
        }else if(hourOfDay >= 12 && hourOfDay < 16){
            timeOfDay = "Afternoon run";
        }else if(hourOfDay >= 16 && hourOfDay < 21){
            timeOfDay = "Evening run";
        }else if(hourOfDay >= 21 && hourOfDay < 24){
            timeOfDay = "Night run";
        }

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MMM d");
        return DATE_FORMAT.format(date) + ", " + timeOfDay;
    }
}
