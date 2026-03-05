package com.eta.authservice.utils;

import com.eta.authservice.model.UserInfoDto;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class ValidationUtil {

    private static final List<String> BLACKLISTED_USERNAMES = Arrays.asList("admin", "user", "system", "root", "guest", "superuser"); // Reserved usernames

    private static final int USERNAME_MIN_LENGTH = 3;
    private static final int USERNAME_MAX_LENGTH = 30;
    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 128;

    private ValidationUtil() {} // Prevent instantiation

    public static void validateUserAttributes(UserInfoDto userInfoDto) throws UserValidationException {
        if (userInfoDto == null) {
            throw new UserValidationException("User info cannot be null"); // Null check
        }

        String username = safeTrim(userInfoDto.getUsername());
        String password = safeTrim(userInfoDto.getPassword());

        validateUsername(username); // Validate username rules
        validatePassword(password, username); // Validate password rules
    }

    // Username validation logic
    private static void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new UserValidationException("Username cannot be empty");
        }

        if (username.length() < USERNAME_MIN_LENGTH || username.length() > USERNAME_MAX_LENGTH) {
            throw new UserValidationException(String.format("Username must be between %d and %d characters", USERNAME_MIN_LENGTH, USERNAME_MAX_LENGTH));
        }

        boolean isEmail = username.contains("@");

        if (isEmail) {
            if (!username.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                throw new UserValidationException("Username must be a valid email address");
            }
        } else {
            if (!username.matches("^[\\w.-]+$")) {
                throw new UserValidationException("Username can only contain letters, numbers, underscores, dots, and hyphens");
            }

            if (BLACKLISTED_USERNAMES.contains(username.toLowerCase(Locale.ROOT))) {
                throw new UserValidationException("This username is not allowed"); // Prevent blacklisted names
            }
        }
    }

    // Password validation logic
    private static void validatePassword(String password, String username) {
        if (password == null || password.isEmpty()) {
            throw new UserValidationException("Password cannot be empty");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            throw new UserValidationException(
                    String.format("Password must be at least %d characters long", PASSWORD_MIN_LENGTH)
            );
        }

        if (password.length() > PASSWORD_MAX_LENGTH) {
            throw new UserValidationException(
                    String.format("Password cannot exceed %d characters", PASSWORD_MAX_LENGTH)
            );
        }

        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\p{Punct}]).+$")) {
            throw new UserValidationException(
                    "Password must include uppercase, lowercase, number, and special character" // Strong password requirement
            );
        }

        if (username != null && password.toLowerCase(Locale.ROOT).contains(username.toLowerCase(Locale.ROOT))) {
            throw new UserValidationException("Password cannot contain username"); // Prevent easy passwords
        }
    }

    private static String safeTrim(String value) {
        return value == null ? null : value.trim(); // Null-safe trim
    }

    public static class UserValidationException extends RuntimeException {
        public UserValidationException(String message) {
            super(message); // Custom exception for validation errors
        }
    }
}
