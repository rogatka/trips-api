package com.example.trips.infrastructure.rest;

import java.util.regex.Pattern;

final class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[\\p{L}0-9_-]+(\\.[\\p{L}0-9_-]+)*@"
                    + "[^-][\\p{L}0-9-]+(\\.[\\p{L}0-9-]+)*(\\.[\\p{L}]{2,})$"
    );

    private EmailValidator() {
    }

    public static boolean isValid(String email) {
        var matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}
