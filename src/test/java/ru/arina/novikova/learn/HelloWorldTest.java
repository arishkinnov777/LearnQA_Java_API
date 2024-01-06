package ru.arina.novikova.learn;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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


}
