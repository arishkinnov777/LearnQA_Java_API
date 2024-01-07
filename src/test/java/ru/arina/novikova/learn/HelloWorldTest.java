package ru.arina.novikova.learn;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    @DisplayName("Ex5: Парсинг JSON")
    public void testJsonParsing() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        List<LinkedHashMap<String, String>> messages = response.getList("messages");
        System.out.println(messages.get(1).get("message"));
    }

    @Test
    @DisplayName("Ex6: Редирект")
    public void testRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        System.out.println(response.getHeader("Location"));
    }

    @Test
    @DisplayName("Ex7: Долгий редирект")
    public void testLongRedirect() {
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int responseCode;
        int countRedirects = 0;
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            String location = response.getHeader("Location");

            responseCode = response.getStatusCode();
            if (location != null) {
                url = location;
                countRedirects++;
            }
        } while (responseCode != 200);

        System.out.println("Response code: " + responseCode + ", Url: " + url + ", count redirects: " + countRedirects);
    }

    @Test
    @DisplayName("Ex8: Токены")
    public void testTokens() throws InterruptedException {

        // 1. Создаем задачу
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token = response.getString("token");
        int seconds = response.getInt("seconds");

        // 2. Делаем один запрос с token ДО того, как задача готова, убеждался в правильности поля status
        response = RestAssured
                .given()
                .param("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        String status = response.getString("status");
        System.out.println("token: " + token + ", seconds: " + seconds + ", Status: " + status);

        // Статус должен быть равен "Job is NOT ready"
        assertEquals("Job is NOT ready", status);

        // 3. Ждем нужное количество секунд
        Thread.sleep(seconds * 1000L);

        // 4. Делаем один запрос c token ПОСЛЕ того, как задача готова, убеждался в правильности поля status и наличии поля result
        response = RestAssured
                .given()
                .param("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        status = response.getString("status");

        // Убеждаемся в правильности поля status
        // Статус должен быть равен "Job is ready"
        assertEquals("Job is ready", status);

        String result = response.getString("result");
        // Убеждаемся в наличии поля result
        Assertions.assertNotNull(result);

        System.out.println("token: " + token + ", seconds: " + seconds + ", Status: " + status + ", Result: " + result);
    }

    @Test
    @DisplayName("Ex9: Подбор пароля")
    public void testPasswordBruteForce() {
        Map<String, String> params = new HashMap<>();
        HashSet<String> passwords = new HashSet<>(Arrays.asList(
                "111111",
                "qazwsx",
                "password",
                "loveme",
                "123456",
                "welcome",
                "access",
                "1234",
                "password1",
                "flower",
                "iloveyou",
                "1234567",
                "1q2w3e4r",
                "123456789",
                "aa123456",
                "qwerty",
                "passw0rd",
                "555555",
                "000000",
                "whatever",
                "123123",
                "ninja",
                "admin",
                "adobe123",
                "qaz2wsx",
                "lovely",
                "hottie",
                "solo",
                "football",
                "Football",
                "michael",
                "12345",
                "shadow",
                "sunshine",
                "mustang",
                "dragon",
                "1234567890",
                "bailey",
                "654321",
                "princess",
                "jesus",
                "baseball",
                "hello",
                "zaq1zaq1",
                "letmein",
                "login",
                "freedom",
                "!@#$%^&*",
                "696969",
                "666666",
                "charlie",
                "donald",
                "monkey",
                "abc123",
                "12345678",
                "starwars",
                "photoshop",
                "superman",
                "888888",
                "qwerty123",
                "azerty",
                "121212",
                "ashley",
                "qwertyuiop",
                "7777777",
                "batman",
                "master",
                "trustno1",
                "123qwe"
        ));

        params.put("login", "super_admin");
        for (String password : passwords) {
            params.put("password", password);
            Response response = RestAssured
                    .given()
                    .body(params)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            Cookie authCookie = response.getDetailedCookie("auth_cookie");
            response = RestAssured
                    .given()
                    .cookie(authCookie)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String result = response.getBody().htmlPath().getString("body");
            if (result.equals("You are authorized")) {
                System.out.println("Password: " + password);
                System.out.println(result);
                break;
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Short string", "There is not short string", "There is long string who not throw assertion error"})
    @DisplayName("Ex10: Тест на короткую фразу")
    public void testLongString(String testedString) {
        int stringLength = testedString.length();
        assertTrue(stringLength > 15, "String length is less than 15. testedString: \"" + testedString + "\"");
    }

    @Test
    @DisplayName("Ex11: Тест запроса на метод cookie")
    public void cookieTest() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String homeWorkCookieValue = response.cookie("HomeWork");
        assertEquals("hw_value", homeWorkCookieValue, "Cookie HomeWork contains wrong value");
    }

    @Test
    @DisplayName("Ex12: Тест запроса на метод header")
    public void headerTest() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        String secretHeaderValue = response.header("x-secret-homework-header");
        assertEquals("Some secret value", secretHeaderValue, "Header x-secret-homework-header contains wrong value");
    }

    @ParameterizedTest
    @MethodSource("userAgentTestProvider")
    @DisplayName("Ex13: UserAgent")
    public void userAgentTest(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice) {

        Response response = RestAssured
                .given()
                .header(new Header("User-Agent", userAgent))
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        String platform = response.jsonPath().getString("platform");
        String browser = response.jsonPath().getString("browser");
        String device = response.jsonPath().getString("device");

        assertEquals(expectedPlatform, platform, "Wrong platform value");
        assertEquals(expectedBrowser, browser, "Wrong browser value");
        assertEquals(expectedDevice, device, "Wrong device value");
    }

    static Stream<Arguments> userAgentTestProvider() {
        return Stream.of(
                arguments(
                        "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile",
                        "No",
                        "Android"
                ),
                arguments(
                        "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "Chrome",
                        "iOS"
                ),
                arguments(
                        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot",
                        "Unknown",
                        "Unknown"
                ),
                arguments(
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web",
                        "Chrome",
                        "No"
                ),
                arguments(
                        "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile",
                        "No",
                        "iPhone"
                )
        );
    }
}
