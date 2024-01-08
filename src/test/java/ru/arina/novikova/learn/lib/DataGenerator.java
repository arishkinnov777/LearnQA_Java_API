package ru.arina.novikova.learn.lib;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class DataGenerator {
    private DataGenerator() {}

    public static String generateRandomEmail() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return String.format("learnqa%s@example.com", timestamp);
    }

    public static Map<String, String> getRegistrationData() {
        Map<String, String> params = new HashMap<>();

        params.put("email", generateRandomEmail());
        params.put("password", "123");
        params.put("username", "learnqa");
        params.put("firstName", "learnqa");
        params.put("lastName", "learnqa");

        return params;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Map<String, String> defaultValues = getRegistrationData();

        Map<String, String> params = new HashMap<>();
        String[] keys = {"username", "password", "firstName", "lastName", "email"};

        Arrays.stream(keys).forEach(key -> {
            if (nonDefaultValues.containsKey(key)) {
                params.put(key, nonDefaultValues.get(key));
            } else {
                params.put(key, defaultValues.get(key));
            }
        });

        return params;
    }
}
