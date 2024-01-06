package ru.arina.novikova.learn;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

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
        Assertions.assertEquals("Job is NOT ready", status);

        // 3. Ждем нужное количество секунд
        Thread.sleep(seconds * 1000);

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
        Assertions.assertEquals("Job is ready", status);

        String result = response.getString("result");
        // Убеждаемся в наличии поля result
        Assertions.assertNotNull(result);

        System.out.println("token: " + token + ", seconds: " + seconds + ", Status: " + status + ", Result: " + result);
    }

}
