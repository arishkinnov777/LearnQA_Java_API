package ru.arina.novikova.learn.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.arina.novikova.learn.lib.ApiCoreRequests;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;
import ru.arina.novikova.learn.lib.DataGenerator;
import ru.arina.novikova.learn.tests.utils.LoginData;

import java.util.HashMap;
import java.util.Map;

import static ru.arina.novikova.learn.tests.utils.UserUtils.createUser;
import static ru.arina.novikova.learn.tests.utils.UserUtils.login;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Ex17.1: Негативные тесты на PUT")
    @DisplayName("Негативный тест на измение данных пользователя, будучи неавторизованными")
    public void testEditUserWithoutAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

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
    @DisplayName("Негативный тест на измение данных пользователя, будучи авторизованными другим пользователем")
    public void testEditUserWithWrongAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        Map<String, String> wrongAuthData = new HashMap<>();
        wrongAuthData.put("email", "vinkotov@example.com");
        wrongAuthData.put("password", "1234");
        LoginData loginData = login(apiCoreRequests, wrongAuthData);

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header(), loginData.cookie(), editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Ex17.3: Негативные тесты на PUT")
    @DisplayName("Негативный тест на измение email пользователя без символа @")
    public void testEditUserWithWrongEmail() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        LoginData loginData = login(apiCoreRequests, params);

        //EDIT
        String newEmail = params.get("email").replaceAll("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header(), loginData.cookie(), editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @Test
    @Description("Ex17.4: Негативные тесты на PUT")
    @DisplayName("Негативный тест на измение firstName пользователя длиной один символ")
    public void testEditUserWithShortFirstName() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        LoginData loginData = login(apiCoreRequests, params);

        //EDIT
        String newName = "F";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makePutRequest(url, loginData.header(), loginData.cookie(), editData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertJsonByName(response, "error", "Too short value for field firstName");
    }

    @Test
    @DisplayName("Позитивный тест на измение данных пользователя")
    public void testEditUserSuccessfully() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        LoginData loginData = login(apiCoreRequests, params);

        //EDIT
        String newName = "Edited first name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        apiCoreRequests.makePutRequest(url, loginData.header(), loginData.cookie(), editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(url, loginData.header(), loginData.cookie());
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
}
