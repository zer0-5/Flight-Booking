package airport;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class RouteTest {

    @BeforeAll
    static void startTest() {
        System.out.println("Starting test");
    }

    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }

    private static Stream<Arguments> usernamesAndPasswords() {
        return Stream.of(
                Arguments.of("London", "Paris", 200),
                Arguments.of("Lisbon", "Porto", 100),
                Arguments.of("Porto", "Lisbon", 100),
                Arguments.of("Paris", "London", 500)
        );
    }

    void serializeAndDeserialize(Route route) {
        byte[] bytes = route.serialize();
        Route r = Route.deserialize(bytes);

        Assertions.assertEquals(r, route);
    }

}
