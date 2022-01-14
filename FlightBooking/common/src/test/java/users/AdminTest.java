package users;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {


    @BeforeAll
    static void startTest() {
        System.out.println("Starting test");
    }

    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }

    //@org.junit.jupiter.api.BeforeEach
    //void setUp() {
    //}

    //@org.junit.jupiter.api.AfterEach
    //void tearDown() {
    //}
}
