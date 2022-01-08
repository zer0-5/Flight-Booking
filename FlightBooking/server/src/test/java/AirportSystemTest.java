import airport.Route;
import exceptions.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
        } catch (RouteAlreadyExistsException e) {
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "Lisbon,London,23" ,"LiSBon,London,30", "Lisbon,LOndoN,30", "LISbon,lonDOn,21"})
    void routeAlreadyExistException(String orig,String dest, int capacity) {
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
        LocalDate date = LocalDate.now();
        addRoute("London","Paris", 1);
        addRoute("Paris","Lisbon", 2);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        List<String> cities2 = new ArrayList<>(Arrays.asList("London","Paris","Lisbon"));
        List<String> cities3 = new ArrayList<>(Arrays.asList("LoNdon","ParIs"));
        List<String> cities4 = new ArrayList<>(Arrays.asList("LoNdon","NOT"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            fail();
        }

        try {
            System.out.println(airportSystem.reserveFlight(UUID.randomUUID(), cities2, date, date));
        } catch (FullFlightException ignored) {
            System.out.println("FAIL: Not added");
        } catch (BookingFlightsNotPossibleException ignored) {fail();}

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities3, date,date);
        } catch (FullFlightException | BookingFlightsNotPossibleException e) {
            fail();
        }

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities4, date,date);
            fail();
        } catch (BookingFlightsNotPossibleException ignored) {}
        catch (FullFlightException ignored) {fail();}
    }

    @org.junit.jupiter.api.Test
    void cancelFlight() {
        LocalDate date = LocalDate.now();
        addRoute("London","Paris", 1);
        addRoute("Paris","Lisbon", 2);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        List<String> cities2 = new ArrayList<>(Arrays.asList("London","Paris","Lisbon"));
        UUID client = UUID.randomUUID();
        UUID reservation1;
        try {
            reservation1 = airportSystem.reserveFlight(client, cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.cancelFlight(client, reservation1);

        } catch (BookingFlightsNotPossibleException | ReservationNotFoundException e) {
            e.printStackTrace();
            fail();
        } catch (ReservationDoesNotBelongToTheClientException e) {
            e.printStackTrace();
        } catch (FullFlightException e) {
            fail();
        }

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities2, date, date);
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
           fail();
        }
    }

    @org.junit.jupiter.api.Test
    void cancelDay() {
        LocalDate date = LocalDate.now();
        addRoute("London","Paris", 1);
        addRoute("Paris","Lisbon", 1);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        List<String> cities2 = new ArrayList<>(Arrays.asList("London","Paris","Lisbon"));
        List<String> cities3 = new ArrayList<>(Arrays.asList("London","Paris"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities2, date,date.plusDays(1));
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            fail();
        }

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date.plusDays(1),date.plusDays(1));
        } catch (BookingFlightsNotPossibleException | FullFlightException e) {
            System.out.println("Nao adicionou voo la");
        }

        try {
            airportSystem.cancelDay(date.plusDays(1));
            airportSystem.cancelDay(date.plusDays(10));
        } catch (DayAlreadyCanceledException e) {
            fail();
        }

        try {
            airportSystem.cancelDay(date.plusDays(1));
            fail();
        } catch (DayAlreadyCanceledException ignored) {}

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities3, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException e) {
            fail();
        }

        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date.plusDays(1),date.plusDays(1));
            fail();
        } catch (FullFlightException ignored) {}
        catch (BookingFlightsNotPossibleException ignored) {fail();}
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 1000, 10000})
    void getRoutes(int N) {
        for(int i = 0; i < N; i++)
            addRoute( String.valueOf(i), "London", i);

        for(int i = 0; i < N; i++)
            addRoute( String.valueOf(i), "Paris", i);

        List<Route> list = airportSystem.getRoutes();
        assert list.size() == N*2;
    }

    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }
}