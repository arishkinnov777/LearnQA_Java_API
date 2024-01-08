package ru.arina.novikova.learn.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;
import ru.arina.novikova.learn.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    @Test
    public void createUserWithExistedEmail() {

        String email = "vinkotov@example.com";

        Map<String, String> params = new HashMap<>();

        params.put("email", email);
        params = DataGenerator.getRegistrationData(params);

        Response response = RestAssured
                .given()
                .body(params)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }
    @Test
    public void createUserSuccessfully() {
        Map<String, String> params = DataGenerator.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(params)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }
}
