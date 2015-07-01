package com.diploma.android.iruntracking.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Validator {
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isWeightValid(String weight) {
        try {
            float validWeight = Float.valueOf(weight);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPasswordValid(String password) {
        return !password.trim().equals("") || !(password.length() < 6);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean isNameValid(String name) {
        return !name.trim().equals("");

    }
}
