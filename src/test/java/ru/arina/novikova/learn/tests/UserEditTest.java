package ru.arina.novikova.learn.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import ru.arina.novikova.learn.lib.ApiCoreRequests;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;
import ru.arina.novikova.learn.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Ex17.1: Негативные тесты на PUT")
    public void testEditUserWithoutAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(params);

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Auth token not supplied");
    }

    @Test
    @Description("Ex17.2: Негативные тесты на PUT")
    public void testEditUserWithWrongAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(params);

        //LOGIN
        Map<String, String> wrongAuthData = new HashMap<>();
        wrongAuthData.put("email", "vinkotov@example.com");
        wrongAuthData.put("password", "1234");
        LoginData loginData = login(wrongAuthData);

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header, loginData.cookie, editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Ex17.3: Негативные тесты на PUT")
    public void testEditUserWithWrongEmail() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(params);

        //LOGIN
        LoginData loginData = login(params);

        //EDIT
        String newEmail = params.get("email").replaceAll("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header, loginData.cookie, editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @Test
    @Description("Ex17.4: Негативные тесты на PUT")
    public void testEditUserWithShortFirstName() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(params);

        //LOGIN
        LoginData loginData = login(params);

        //EDIT
        String newName = "F";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header, loginData.cookie, editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertJsonByName(response, "error", "Too short value for field firstName");
    }

    @Test
    public void testEditUserSuccessfully() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(params);

        //LOGIN
        LoginData loginData = login(params);

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        apiCoreRequests.makePutRequest(url, loginData.header, loginData.cookie, editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(url, loginData.header, loginData.cookie);
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    private String createUser(Map<String, String> params) {
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);
        return response.jsonPath().getString("id");
    }

    private LoginData login(Map<String, String> params) {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", params.get("email"));
        authData.put("password", params.get("password"));

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String cookie = this.getCookie(responseAuth, "auth_sid");
        String header = this.getHeader(responseAuth, "x-csrf-token");
        return new LoginData(header, cookie);
    }

    private record LoginData (String header, String cookie) {}
}
