package ru.arina.novikova.learn.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.arina.novikova.learn.lib.ApiCoreRequests;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void testGetUserDetailsNotAuth() {
        Response response = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(response, "username");

        String[] notExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonNotHasFields(response, notExpectedFields);
    }

    @Test
    public void testGetUserDetailsAuthSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(response, "auth_sid");
        String header = this.getHeader(response, "x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("Ex16: Запрос данных другого пользователя")
    public void testGetUserDetailsAuthAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String cookie = this.getCookie(response, "auth_sid");
        String header = this.getHeader(response, "x-csrf-token");

        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/1", header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");

        String[] notExpectedFields = {"firstName", "lastName", "email"};
        Assertions.assertJsonNotHasFields(responseUserData, notExpectedFields);
    }
}
