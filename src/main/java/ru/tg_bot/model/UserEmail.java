package ru.tg_bot.model;

import jakarta.validation.constraints.Email;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class UserEmail {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final String value;

    private UserEmail(String value) {
        this.value = value;
    }

    public static UserEmail of(String value) {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return new UserEmail(value);
    }
}
