package ru.arina.novikova.learn.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;
import ru.arina.novikova.learn.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    @Test
    public void testEditUser() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(params)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        String userId = response.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", params.get("email"));
        authData.put("password", params.get("password"));

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String cookie = this.getCookie(responseAuth, "auth_sid");
        String header = this.getHeader(responseAuth, "x-csrf-token");

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .body(editData)
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .put(String.format("https://playground.learnqa.ru/api/user/%s", userId))
                .andReturn();


        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get(String.format("https://playground.learnqa.ru/api/user/%s", userId))
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
}
