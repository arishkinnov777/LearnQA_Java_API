package ru.arina.novikova.learn.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.arina.novikova.learn.lib.ApiCoreRequests;
import ru.arina.novikova.learn.lib.Assertions;
import ru.arina.novikova.learn.lib.BaseTestCase;
import ru.arina.novikova.learn.lib.DataGenerator;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    public void createUserWithExistedEmail() {

        String email = "vinkotov@example.com";

        Map<String, String> params = new HashMap<>();

        params.put("email", email);
        params = DataGenerator.getRegistrationData(params);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("Users with email '%s' already exists", email));
    }

    @Test
    @Description("Ex15.1: Тесты на метод user")
    public void createUserWithCorruptedEmail() {

        String email = "vinkotovexample.com";

        Map<String, String> params = new HashMap<>();

        params.put("email", email);
        params = DataGenerator.getRegistrationData(params);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @ParameterizedTest
    @Description("Ex15.2: Тесты на метод user")
    @ValueSource(strings = {"username", "password", "firstName", "lastName", "email"})
    public void createUserWithoutField(String skippedField) {
        Map<String, String> params = DataGenerator.getRegistrationData();

        params.remove(skippedField);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, String.format("The following required params are missed: %s", skippedField));
    }

    @Test
    @Description("Ex15.3: Тесты на метод user")
    public void createUserWithShortFirstName() {

        String firstName = "v";
        Map<String, String> params = new HashMap<>();

        params.put("firstName", firstName);
        params = DataGenerator.getRegistrationData(params);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
    }

    @Test
    @Description("Ex15.4: Тесты на метод user")
    public void createUserWithLongFirstName() {

        String firstName = "First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.First name more than 250 characters.";
        Map<String, String> params = new HashMap<>();

        params.put("firstName", firstName);
        params = DataGenerator.getRegistrationData(params);

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
    }

    @Test
    public void createUserSuccessfully() {
        Map<String, String> params = DataGenerator.getRegistrationData();

        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", params);

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }
}
