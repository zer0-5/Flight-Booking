import airport.Route;
import exceptions.RouteAlreadyExistsException;
import exceptions.RouteDoesntExistException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import system.AirportSystem;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AirportSystemTest {

    private AirportSystem airportSystem;

    @BeforeAll
    static void startTest() {
        System.out.println("Starting test");
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        airportSystem = new AirportSystem();
        System.out.println("---- TEST ----");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.out.println("---------------");
    }

    @DisplayName("Add Route")
    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "London,Paris,1", "Lisbon,Paris,23"})
    public void addRoute(String orig, String dest, int capacity) {
        try {
            System.out.println("Add route: " + orig + "-> " + dest + " ("+ capacity +")");
            airportSystem.addRoute(orig, dest, capacity);
            List<Route> list = airportSystem.getRoutes();
            assert list.size() == 1;
        } catch (RouteAlreadyExistsException e) {
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "Lisbon,London,23" ,"LiSBon,London,30", "Lisbon,LOndoN,30", "LISbon,lonDOn,21"})
    void routeAlreadyExistException(String orig,String dest, int capacity) {
        System.out.println("Add route: Lisbon -> London (30)");
        addRoute("Lisbon","London",30);
        try {
            System.out.println("TRY: Add route: " + orig + "-> " + dest + " ("+ capacity +")");
            airportSystem.addRoute(orig, dest, capacity);
        } catch (RouteAlreadyExistsException e) {
            System.out.println("FAIL: Not added");
            return;
        }
        fail();
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "London,Paris,1", "Lisbon,Paris,23"})
    void getRoute(String orig, String dest, int capacity) {
        try {
            System.out.println("Add route: " + orig + "-> " + dest + " ("+ capacity +")");
            airportSystem.addRoute(orig, dest, capacity);
            System.out.println("GET route: " + orig + "-> " + dest);
            Route route1 = airportSystem.getRoute(orig, dest);
            assert route1.origin.equals(orig);
            assert route1.destination.equals(dest);
            assert route1.capacity == capacity;
            System.out.println("GET route: " + orig.toUpperCase() + "-> " + dest.toLowerCase());
            Route route2 = airportSystem.getRoute(orig.toUpperCase(), dest.toLowerCase());
            assert route2.origin.equals(orig);
            assert route2.destination.equals(dest);
            assert route2.capacity == capacity;
            System.out.println("GET route: " + orig.toLowerCase() + "-> " + dest.toUpperCase());
            Route route3 = airportSystem.getRoute(orig.toLowerCase(), dest.toUpperCase());
            assert route3.origin.equals(orig);
            assert route3.destination.equals(dest);
            assert route3.capacity == capacity;
        } catch (RouteAlreadyExistsException | RouteDoesntExistException e) {
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,Paris", "Paris,Lisbon"})
    void routeDoesntExistException(String orig, String dest) {
        System.out.println("Add route: Lisbon -> London (30)");
        addRoute("Lisbon","London",30);
        try {
            System.out.println("GET route: " + orig + "-> " + dest);
            airportSystem.getRoute(orig, dest);
        } catch (RouteDoesntExistException ignored) {
            System.out.println("FAIL: Exception thrown");
        }
    }

    @org.junit.jupiter.api.Test
    void reserveFlight() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void cancelFlight() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void cancelDay() {
        fail("Not yet implemented");
    }

    @org.junit.jupiter.api.Test
    void getRoutes() {
        fail("Not yet implemented");
    }

    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }
}