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

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Ex18.1: Тесты на DELETE")
    @DisplayName("Негативный тест на удаление пользователя без авторизации")
    public void testDeleteUserWithoutAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //DELETE
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makeDeleteRequest(url);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Auth token not supplied");
    }

    @Test
    @Description("Ex18.2: Тесты на DELETE")
    @DisplayName("Негативный тест на удаление пользователя с идентификатором 2")
    public void testDeleteProtectedUser() {

        //LOGIN
        Map<String, String> wrongAuthData = new HashMap<>();
        wrongAuthData.put("email", "vinkotov@example.com");
        wrongAuthData.put("password", "1234");
        LoginData loginData = login(apiCoreRequests, wrongAuthData);

        //DELETE
        String url = "https://playground.learnqa.ru/api/user/2";
        Response response = apiCoreRequests.makeDeleteRequest(url, loginData.header(), loginData.cookie());

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Ex18.3: Тесты на DELETE")
    @DisplayName("Негативный тест на удаление пользователя будучи авторизованными другим пользователем")
    public void testDeleteUserWithWrongAuth() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        Map<String, String> wrongAuthData = new HashMap<>();
        wrongAuthData.put("email", "vinkotov@example.com");
        wrongAuthData.put("password", "1234");
        LoginData loginData = login(apiCoreRequests, wrongAuthData);

        //DELETE
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        Response response = apiCoreRequests.makeDeleteRequest(url, loginData.header(), loginData.cookie());

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Ex18.4: Тесты на DELETE")
    @DisplayName("Позитивный тест на удаление пользователя")
    public void testDeleteUserSuccessfully() {

        //CREATE
        Map<String, String> params = DataGenerator.getRegistrationData();
        String userId = createUser(apiCoreRequests, params);

        //LOGIN
        LoginData loginData = login(apiCoreRequests, params);

        //DELETE
        String url = String.format("https://playground.learnqa.ru/api/user/%s", userId);
        apiCoreRequests.makeDeleteRequest(url, loginData.header(), loginData.cookie());

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(url, loginData.header(), loginData.cookie());
        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");

    }
}
