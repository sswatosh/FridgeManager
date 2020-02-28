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
    public void testGetFridges() {
        assertEquals(
            "[{\"id\":1,\"name\":\"Kitchen\"}]",
            GET("/api/fridges")
        );
    }

    @Test
    @Order(4)
    public void testGetFridge() {
        assertEquals(
            "{\"id\":1,\"name\":\"Kitchen\"}",
            GET("/api/fridges/1")
        );
    }

    @Test
    @Order(5)
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

    @Test
    @Order(6)
    public void testGetItemsInitiallyEmpty() {
        assertEquals("[]", GET("/api/fridges/1/items"));
    }

    @Test
    @Order(7)
    public void testAddItem() {
        assertEquals(
            "{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}",
            POST("/api/fridges/1/items", "{\"type\":\"SODA\",\"name\":\"Dr Pepper\"}")
        );
    }

    @Test
    @Order(8)
    public void testGetItems() {
        assertEquals(
            "[{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}]",
            GET("/api/fridges/1/items")
        );
    }

    @Test
    @Order(9)
    public void testGetItem() {
        assertEquals(
            "{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}",
            GET("/api/fridges/1/items/1")
        );
    }

    @Test
    @Order(10)
    public void testUpdateItem() {
        assertEquals(
            "",
            PATCH("/api/fridges/1/items/1", "{\"name\":\"Mtn Dew\"}")
        );
        assertEquals(
            "{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Mtn Dew\"}",
            GET("/api/fridges/1/items/1")
        );
    }

    @Test
    @Order(11)
    public void testDeleteItem() {
        assertEquals(
            "",
            DELETE("/api/fridges/1/items/1")
        );
        assertEquals(
            "[]",
            GET("/api/fridges/1/items/")
        );
    }

    private String GET(String path) {
        return mainApp.getHttpClient().getText(path);
    }

    private String DELETE(String path) {
        return mainApp.getHttpClient().deleteText(path);
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
