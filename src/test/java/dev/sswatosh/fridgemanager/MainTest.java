package dev.sswatosh.fridgemanager;

import org.junit.jupiter.api.Test;
import ratpack.test.MainClassApplicationUnderTest;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    MainClassApplicationUnderTest mainApp = new MainClassApplicationUnderTest(Main.class);

    @Test
    public void getHelloWorld() {
        assertEquals("Hello World!", mainApp.getHttpClient().getText("/"));
    }

    @Test
    public void getHelloName() {
        assertEquals("Hello Scott!", mainApp.getHttpClient().getText("/Scott"));
    }
}
