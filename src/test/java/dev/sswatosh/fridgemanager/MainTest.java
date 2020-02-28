package dev.sswatosh.fridgemanager;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ratpack.http.HttpMethod;
import ratpack.test.MainClassApplicationUnderTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainTest {

    MainClassApplicationUnderTest mainApp = new MainClassApplicationUnderTest(Main.class);

    @Test
    @Order(1)
    public void testFridgesInitiallyEmpty() {
        assertEquals("[]", GET("/api/fridges"));
    }

    @Test
    @Order(2)
    public void testAddFridge() {
        assertEquals(
            "{\"id\":1,\"name\":\"Kitchen\"}",
            POST("/api/fridges", "{\"name\":\"Kitchen\"}")
        );
    }

    @Test
    @Order(3)
    public void testGetFridge() {
        assertEquals(
            "{\"id\":1,\"name\":\"Kitchen\"}",
            GET("/api/fridges/1")
        );
    }

    @Test
    @Order(4)
    public void testUpdateFridge() {
        assertEquals(
            "",
            PATCH("/api/fridges/1", "{\"name\":\"Basement\"}")
        );
        assertEquals(
            "{\"id\":1,\"name\":\"Basement\"}",
            GET("/api/fridges/1")
        );
    }

    private String GET(String path) {
        return mainApp.getHttpClient().getText(path);
    }

    private String POST(String path, String requestBody) {
        return requestWithBody(path, requestBody, HttpMethod.POST);
    }

    private String PATCH(String path, String requestBody) {
        return requestWithBody(path, requestBody, HttpMethod.PATCH);
    }

    private String requestWithBody(String path, String requestBody, HttpMethod method) {
        return mainApp.getHttpClient().request(path, requestSpec -> {
            requestSpec.getBody().text(requestBody);
            requestSpec.method(method);
            requestSpec.getHeaders().set("Content-Type", "application/json");
        }).getBody().getText();
    }
}
