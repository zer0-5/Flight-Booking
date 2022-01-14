package airport;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import users.Admin;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class FlightTest {


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

    Set<Flight> voos;
    Admin admin;

    private Stream<Arguments> usernamesAndPasswords() {
        admin = new Admin("jaskldfj", "dsafadsf");
        Route r1 = new Route("Ola", "dajfadsf", 30);
        Route r2 = new Route("Ola", "dajfadsf", 30);
        Route r3 = new Route("Ola", "dajfadsf", 30);
        UUID idReservation = UUID.randomUUID();
        Set<UUID> set = new HashSet<>();
        set.add(idReservation);

        Flight f1 = new Flight(UUID.randomUUID(), r1, LocalDate.now(), set);
        voos = new HashSet<>();
        voos.add(f1);
        Reservation reservation1 = new Reservation(admin, voos);

        return Stream.of(
                Arguments.of(UUID.randomUUID(), r1, LocalDate.now(), reservation1),
                Arguments.of(UUID.randomUUID(), r2, LocalDate.now(), reservation1),
                Arguments.of(UUID.randomUUID(), r3, LocalDate.now().plusDays(3), reservation1)
        );
    }


    @ParameterizedTest
    @MethodSource("usernamesAndPasswords")
    void serizalizeAndDeserialize(UUID id, Route route, LocalDate date, Set<UUID> reservations) {
        //Flight flight = new Flight(admin, voos);
    }
}
