package ru.arina.novikova.learn.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;

import java.util.HashMap;
import java.util.Map;

public class UserAuthTest extends BaseTestCase {
    private String cookie;
    private String header;
    private int userIdOnAuth;

    @BeforeEach
    public void loginUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response response = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(response, "auth_sid");
        this.header = this.getHeader(response, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(response, "user_id");
    }

    @Test
    public void testAuthUser() {
        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition) {

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        switch (condition) {
            case "cookie" -> spec.cookie("auth_sid", this.cookie);
            case "headers" -> spec.header("x-csrf-token", this.header);
            default -> throw new IllegalArgumentException(String.format("Condition value \"%s\" is unknown", condition));
        }

        Response response = spec.get().andReturn();
        Assertions.assertJsonByName(response, "user_id", 0);
    }
}
