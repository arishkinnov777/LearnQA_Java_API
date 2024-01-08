package ru.arina.novikova.learn.lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class Assertions {

    private Assertions() {}

    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));
        int value = response.jsonPath().getInt(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertJsonByName(Response response, String name, String expectedValue) {
        response.then().assertThat().body("$", hasKey(name));
        String value = response.jsonPath().getString(name);

        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertJsonHasField(Response response, String key) {
        response.then().assertThat().body("$", hasKey(key));
    }

    public static void assertJsonHasFields(Response response, String[] keys) {
        for (String key : keys) {
            assertJsonHasField(response, key);
        }
    }

    public static void assertJsonNotHasField(Response response, String key) {
        response.then().assertThat().body("$", not(hasKey(key)));
    }

    public static void assertJsonNotHasFields(Response response, String[] keys) {
        for (String key : keys) {
            assertJsonNotHasField(response, key);
        }
    }

    public static void assertResponseTextEquals(Response response, String expectedText) {
        assertEquals(expectedText, response.asString(), "Response text is not equals expected");
    }

    public static void assertResponseCodeEquals(Response response, int expectedCode) {
        assertEquals(expectedCode, response.getStatusCode(), "Response status code is not equals expected");
    }
}
