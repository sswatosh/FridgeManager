package dev.sswatosh.fridgemanager;

import dev.sswatosh.fridgemanager.auth.TokenStore;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import ratpack.http.HttpMethod;
import ratpack.http.Status;
import ratpack.http.client.ReceivedResponse;
import ratpack.http.client.RequestSpec;
import ratpack.test.MainClassApplicationUnderTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MainTest {
    private static final String ADULT = TokenStore.ADULT_TOKEN;
    private static final String CHILD = TokenStore.CHILD_TOKEN;

    private ReceivedResponse response;

    MainClassApplicationUnderTest mainApp = new MainClassApplicationUnderTest(Main.class);

    @Test
    @Order(1)
    public void testFridgesInitiallyEmpty() {
        GET("/api/fridges", ADULT);
        checkBody("[]");
        checkStatus(Status.OK);

        GET("/api/fridges", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(2)
    public void testAddFridge() {
        POST("/api/fridges", "{\"name\":\"Kitchen\"}", ADULT);
        checkBody("{\"id\":1,\"name\":\"Kitchen\"}");
        checkStatus(Status.OK);

        POST("/api/fridges", "{\"name\":\"Kitchen\"}", CHILD);
        checkStatus(Status.FORBIDDEN);
    }

    @Test
    @Order(3)
    public void testGetFridges() {
        GET("/api/fridges", ADULT);
        checkBody("[{\"id\":1,\"name\":\"Kitchen\"}]");
        checkStatus(Status.OK);

        GET("/api/fridges", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(4)
    public void testGetFridge() {
        GET("/api/fridges/1", ADULT);
        checkBody("{\"id\":1,\"name\":\"Kitchen\"}");
        checkStatus(Status.OK);

        GET("/api/fridges/1", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(5)
    public void testUpdateFridge() {
        PATCH("/api/fridges/1", "{\"name\":\"Basement\"}", CHILD);
        checkStatus(Status.FORBIDDEN);

        GET("/api/fridges/1", CHILD);
        checkBody("{\"id\":1,\"name\":\"Kitchen\"}");

        PATCH("/api/fridges/1", "{\"name\":\"Basement\"}", ADULT);
        checkBody("");
        checkStatus(Status.NO_CONTENT);

        GET("/api/fridges/1", ADULT);
        checkBody("{\"id\":1,\"name\":\"Basement\"}");
    }

    @Test
    @Order(6)
    public void testGetItemsInitiallyEmpty() {
        GET("/api/fridges/1/items", ADULT);
        checkBody("[]");
        checkStatus(Status.OK);

        GET("/api/fridges/1/items", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(7)
    public void testAddItem() {
        POST("/api/fridges/1/items", "{\"type\":\"SODA\",\"name\":\"Dr Pepper\"}", CHILD);
        checkStatus(Status.FORBIDDEN);

        GET("/api/fridges/1/items", CHILD);
        checkBody("[]");

        POST("/api/fridges/1/items", "{\"type\":\"SODA\",\"name\":\"Dr Pepper\"}", ADULT);
        checkBody("{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}");
        checkStatus(Status.OK);
    }

    @Test
    @Order(8)
    public void testGetItems() {
        GET("/api/fridges/1/items", ADULT);
        checkBody("[{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}]");
        checkStatus(Status.OK);

        GET("/api/fridges/1/items", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(9)
    public void testGetItem() {
        GET("/api/fridges/1/items/1", ADULT);
        checkBody("{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}");
        checkStatus(Status.OK);

        GET("/api/fridges/1/items/1", CHILD);
        checkStatus(Status.OK);
    }

    @Test
    @Order(10)
    public void testUpdateItem() {
        PATCH("/api/fridges/1/items/1", "{\"name\":\"Mtn Dew\"}", CHILD);
        checkStatus(Status.FORBIDDEN);

        GET("/api/fridges/1/items/1", CHILD);
        checkBody("{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Pepper\"}");

        PATCH("/api/fridges/1/items/1", "{\"name\":\"Mtn Dew\"}", ADULT);
        checkBody("");
        checkStatus(Status.NO_CONTENT);

        GET("/api/fridges/1/items/1", ADULT);
        checkBody("{\"id\":1,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Mtn Dew\"}");
    }

    @Test
    @Order(11)
    public void testDeleteItem() {
        POST("/api/fridges/1/items", "{\"type\":\"SODA\",\"name\":\"Dr Coke\"}", ADULT);
        checkBody("{\"id\":2,\"fridgeId\":1,\"type\":\"SODA\",\"name\":\"Dr Coke\"}");
        checkStatus(Status.OK);

        DELETE("/api/fridges/1/items/1", ADULT);
        checkBody("");
        checkStatus(Status.NO_CONTENT);

        DELETE("/api/fridges/1/items/2", CHILD);
        checkBody("");
        checkStatus(Status.NO_CONTENT);

        GET("/api/fridges/1/items/", ADULT);
        checkBody("[]");
    }

    @Test
    @Order(12)
    public void testBadRequest() {
        POST("/api/fridges", "{}", ADULT);
        checkStatus(Status.BAD_REQUEST);
    }

    @Test
    @Order(13)
    public void testMissingEntity() {
        GET("/api/fridges/100", ADULT);
        checkStatus(Status.NOT_FOUND);

        GET("/api/fridges/1/items/100", ADULT);
        checkStatus(Status.NOT_FOUND);
    }

    private void GET(String path, String token) {
        makeRequest(path, "", HttpMethod.GET, token);
    }

    private void DELETE(String path, String token) {
        makeRequest(path, "", HttpMethod.DELETE, token);
    }

    private void POST(String path, String requestBody, String token) {
        makeRequest(path, requestBody, HttpMethod.POST, token);
    }

    private void PATCH(String path, String requestBody, String token) {
        makeRequest(path, requestBody, HttpMethod.PATCH, token);
    }

    private void makeRequest(String path, String requestBody, HttpMethod method, String token) {
        this.response = mainApp.getHttpClient().request(path, requestSpec -> {
            requestSpec.getBody().text(requestBody);
            requestSpec.method(method);
            requestSpec.getHeaders().set("Content-Type", "application/json");
            addToken(requestSpec, token);
        });
    }

    private void addToken(RequestSpec requestSpec, String token) {
        requestSpec.getHeaders().set("Authorization", "Bearer " + token);
    }

    private void checkBody(String bodyText) {
        assertThat(response.getBody().getText(), equalTo(bodyText));
    }

    private void checkStatus(Status status) {
        assertThat(response.getStatus(), equalTo(status));
    }
}
