package ru.arina.novikova.learn.tests.utils;

import io.restassured.http.Headers;
import io.restassured.response.Response;
import ru.arina.novikova.learn.lib.ApiCoreRequests;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class UserUtils {
    private UserUtils() {}

    public static String createUser(ApiCoreRequests apiCoreRequests, Map<String, String> params) {
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);
        return response.jsonPath().getString("id");
    }

    public static LoginData login(ApiCoreRequests apiCoreRequests, Map<String, String> params) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", params.get("email"));
        authData.put("password", params.get("password"));

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String cookie = getCookie(responseAuth, "auth_sid");
        String header = getHeader(responseAuth, "x-csrf-token");
        return new LoginData(header, cookie);
    }

    private static String getHeader(Response response, String name) {
        Headers headers = response.getHeaders();
        assertTrue(headers.hasHeaderWithName(name), String.format("Response doesn't have header with name %s", name));
        return headers.getValue(name);
    }

    private static String getCookie(Response response, String name) {
        Map<String, String> cookies = response.cookies();
        assertTrue(cookies.containsKey(name), String.format("Response doesn't have cookie with name %s", name));
        return cookies.get(name);
    }
}
