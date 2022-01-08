import airport.Route;
import exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import server.src.main.InvalidRouteException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AirportSystemTest {

    private AirportSystem airportSystem;
    //Used to test.
    private LocalDate date ;


    @BeforeAll
    static void startTest() {
        System.out.println("Starting test");
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        airportSystem = new AirportSystem();
        date = LocalDate.now();
        System.out.println("---- TEST ----");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.out.println("---------------");
    }

    // -------------------- Add Route -------------------

    @DisplayName("Add Route")
    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "London,Paris,1", "Lisbon,Paris,23"})
    public void addRoute(String orig, String dest, int capacity) {
        try {
            System.out.println("Add route: " + orig + "-> " + dest + " ("+ capacity +")");
            airportSystem.addRoute(orig, dest, capacity);
        } catch (RouteAlreadyExistsException | InvalidRouteException e) {
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "Lisbon,London,23" ,"LiSBon,London,30", "Lisbon,LOndoN,30", "LISbon,lonDOn,21"})
    void routeAlreadyExistException(String orig,String dest, int capacity) {
        addRoute("Lisbon","London",30);
        Assertions.assertThrows(RouteAlreadyExistsException.class, () -> {
            airportSystem.addRoute(orig, dest, capacity);
        });
    }
    @ParameterizedTest
    @CsvSource({"Lisbon,Lisbon,30"})
    void routeSameOriginDestinationException(String orig,String dest, int capacity) {
        Assertions.assertThrows(InvalidRouteException.class, () -> {
            airportSystem.addRoute(orig, dest, capacity);
        });

    }
    //--------------------- Get Routes ----------------------
    /**
     * Test  to verify if the route stores with case insensivity.
     * We create one root, and test if we can get roots with names that have other "camel cases" in name.
     * In case the route doesn't exist, an excpetion is thrown.
     *
     * @param orig
     * @param dest
     * @param capacity
     */
    @ParameterizedTest
    @CsvSource({"Lisbon,London,30", "London,Paris,1", "Lisbon,Paris,23"})
    void getRoute(String orig, String dest, int capacity) {
        try {
            System.out.println("Add route: " + orig + "-> " + dest + " ("+ capacity +")");
            airportSystem.addRoute(orig, dest, capacity);
            System.out.println("GET route: " + orig + "-> " + dest);
            List<Route> routes_tested = new ArrayList<>();
            routes_tested.add(airportSystem.getRoute(orig, dest));
            System.out.println("GET route: " + orig.toUpperCase() + "-> " + dest.toLowerCase());
            routes_tested.add(airportSystem.getRoute(orig.toUpperCase(), dest.toLowerCase()));
            System.out.println("GET route: " + orig.toLowerCase() + "-> " + dest.toUpperCase());
            routes_tested.add(airportSystem.getRoute(orig.toLowerCase(), dest.toUpperCase()));
            for (Route tested : routes_tested){
                tested.origin.equals(orig);
                tested.destination.equals(dest);
                assert tested.capacity == capacity;
            }
        } catch (RouteAlreadyExistsException | RouteDoesntExistException | InvalidRouteException e) {
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"Lisbon,Paris", "Paris,Lisbon"})
    public void routeDoesntExistException(String orig, String dest) {
        System.out.println("Add route: Lisbon -> London (30)");
        addRoute("Lisbon","London",30);
        Assertions.assertThrows(RouteDoesntExistException.class, () -> {
            System.out.println("GET route: " + orig + "-> " + dest);
            airportSystem.getRoute(orig, dest);
        });

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


    //---------------------- Reservation Flights ----------------
    @org.junit.jupiter.api.Test
    void reserveFlightValid(){
        addRoute("Paris","Lisbon", 2);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void reserveFlightValid_DifferentDays(){
        addRoute("Paris","Lisbon", 1);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date.plusDays(1));
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            fail();
        }
    }
    /**
     * Exception thrown when the flight has no free seats.
     */
    @org.junit.jupiter.api.Test
    void reserveFligth_FullFligthExcpeption(){
        addRoute("Paris","Lisbon", 2);
        List<String> cities1 = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            fail();
        }
        Assertions.assertThrows(FullFlightException.class, () -> {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
        });
    }


    /**
     * Test to check if the pre-reservations are removed if the full flight isn't possible.
     * Example of the tested situation:
     * Flight 1:      B -> C
     * Flight 2: A -> B -> C
     * Flight 3: A -> B
     * The flight 1 is full, and when a client wants to reserve the flight 2, it pre-reserves the flight A -> B.
     * So, the reservation of flight 2 isn't possible, B -> C is full, and the system should remove the pre-reservation for the flight A->B.
     * We test if it's possible to reserve a flight after a pre-reservation is removed.
     */
    @org.junit.jupiter.api.Test
    void reserveFligth_preReservationCanceledExcpeption(){
        initRoutes_LondonParisLisbon();
        List<String> cities1 = new ArrayList<>(Arrays.asList          ("Paris","Lisbon"));
        List<String> cities2 = new ArrayList<>(Arrays.asList("London","Paris","Lisbon"));
        List<String> cities3 = new ArrayList<>(Arrays.asList("LoNdon","ParIs"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities1, date,date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities2, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException ignored) {
            System.out.println("One flight isn't possible, the pre-reservations should be removed.");
        }


        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities3, date,date);
        } catch (FullFlightException | BookingFlightsNotPossibleException e) {
            fail();
        }

    }

    /**
     * Method to create a stream composed by list of cities that don't exist in system and current date.
     * @return
     */
    private static Stream<Arguments> citiesDontExist() {
        List<Arguments> listOfArguments = new ArrayList<>();
        listOfArguments.add(Arguments.of(new ArrayList<>(Arrays.asList("London","NotCity"))));
        listOfArguments.add(Arguments.of(new ArrayList<>(Arrays.asList("NotCity", "London"))));
        listOfArguments.add(Arguments.of(new ArrayList<>(Arrays.asList("NotCity","NotCity"))));
        return listOfArguments.stream();
    }
    /*
     * Exception thrown when the flight has no free seats.
     * @param cities Cities that don't exist in system.
     * @param date Date to search the flight. We only need one in this test, because we don't test the dates.
     */
    @ParameterizedTest
    @MethodSource("citiesDontExist")
    void reserveFligth_BookNotPossible_CityNotExist(List<String> cities){
        initRoutes_LondonParisLisbon();
        Assertions.assertThrows(BookingFlightsNotPossibleException.class, () -> {
            airportSystem.reserveFlight(UUID.randomUUID(), cities, date,date);
        });
    }

    /**
     * Este é o teste que tínhamos, acho que o consegui dividir nos testes anteriores.
     * Falta teste para cidade que não existe.
     */
    @org.junit.jupiter.api.Test
    void reserveFlight() {
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
    //---------------------- Cancel Flights ----------------

    /**
     * Cancel one flight and try to reserve again.
     */
    @org.junit.jupiter.api.Test
    void cancelFlightTest(){
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris","Lisbon"));
        UUID client = UUID.randomUUID();
        UUID reservation;
        try {
            reservation = airportSystem.reserveFlight(client, cities, date,date);
            airportSystem.cancelFlight(client, reservation);
            airportSystem.reserveFlight(client, cities, date,date);
        } catch (BookingFlightsNotPossibleException | FullFlightException | ReservationNotFoundException | ReservationDoesNotBelongToTheClientException e) {
            fail();
        }
    }

    /**
     * Cancel one flight in a connection and try to reserve the second flight.
     */
    @org.junit.jupiter.api.Test
    void cancelConnectionFlightTest(){
        initRoutes_LondonParisLisbon();
        List<String> cities1 = new ArrayList<>(Arrays.asList("London","Paris", "Lisbon"));
        List<String> cities2 = new ArrayList<>(Arrays.asList(        "Paris", "Lisbon"));
        UUID client = UUID.randomUUID();
        UUID reservation;
        try {

            reservation = airportSystem.reserveFlight(client, cities1, date,date);
            airportSystem.cancelFlight(client, reservation);
            airportSystem.reserveFlight(client, cities2, date,date);

        } catch (BookingFlightsNotPossibleException | FullFlightException | ReservationNotFoundException | ReservationDoesNotBelongToTheClientException e) {
            fail();
        }
    }
    @org.junit.jupiter.api.Test
    void cancelFlight_NotCurrentUserException() {
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));

        Assertions.assertThrows(ReservationDoesNotBelongToTheClientException.class, () -> {
            UUID reservation = airportSystem.reserveFlight(UUID.randomUUID(), cities, date, date);
            airportSystem.cancelFlight(UUID.randomUUID(), reservation);
        });
    }
    @org.junit.jupiter.api.Test
    void cancelFlight_ReservationNotFoundException() {
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));

        Assertions.assertThrows(ReservationNotFoundException.class, () -> {
            airportSystem.cancelFlight(UUID.randomUUID(), UUID.randomUUID());
        });
    }


    /**
     * Acho que já está separado nos testes para trás.
     */
    @org.junit.jupiter.api.Test
    void cancelFlight() {
        LocalDate date = LocalDate.now();
        addRoute("London","Paris", 1);
        addRoute("Paris","Lisbon", 2);
        List<String> cities1 = new ArrayList<>(Arrays.asList(          "Paris","Lisbon"));
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
    //---------------------- Cancel Days ----------------


    @org.junit.jupiter.api.Test
    void cancelDayTwoTimes(){
        Assertions.assertThrows(DayAlreadyCanceledException.class, () -> {
            airportSystem.cancelDay(date);
            airportSystem.cancelDay(date);
        });
    }

    /**
     * Devíamos alterar o nome cancelFlight para cancelReservation, acho.
     */
    @org.junit.jupiter.api.Test
    void cancelReservationAfterCancelDay(){
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
        UUID client = UUID.randomUUID();
        Assertions.assertThrows(ReservationNotFoundException.class, () -> {
            UUID reservation = airportSystem.reserveFlight(client, cities, date, date);
            airportSystem.cancelDay(date);
            airportSystem.cancelFlight(client, reservation);
        });

        }

    /**
     * Este está mal... Mas teríamos de mudar umas coisas na implementação.
     * Acho que é melhor juntar as duas exceções que o reserFlight dá.
     */
    @org.junit.jupiter.api.Test
    void cancelReservationAfterCancelDay1(){
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
        Assertions.assertThrows(FullFlightException.class, () -> {
            airportSystem.cancelDay(date);
            airportSystem.reserveFlight(UUID.randomUUID(), cities, date, date);
        });
    }

    @org.junit.jupiter.api.Test
    void cancelDayWithFligths(){
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
        try {
            airportSystem.reserveFlight(UUID.randomUUID(), cities, date, date);
            airportSystem.cancelDay(date);

        } catch (BookingFlightsNotPossibleException | FullFlightException | DayAlreadyCanceledException e) {
            fail();
        }
    }

    @org.junit.jupiter.api.Test
    void addFlightAferDayCanceled(){
        initRoutes_LondonParisLisbon();
        List<String> cities = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
        try {
            airportSystem.cancelDay(date.plusDays(1));
            airportSystem.reserveFlight(UUID.randomUUID(), cities, date, date);
        } catch (BookingFlightsNotPossibleException | FullFlightException | DayAlreadyCanceledException e) {
            fail();
        }
    }

    /**
     * Também acho que já está tudo feito atrás.
     */
    @org.junit.jupiter.api.Test
    void cancelDay() {
        LocalDate date = LocalDate.now();
        initRoutes_LondonParisLisbon();
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


    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }

    /**
     * Private method to initialize airport system.
     * Capacity of each flight is 1.
     */
    private void initRoutes_LondonParisLisbon(){
        System.out.println("Add route: London -> Paris (1)");
        addRoute("London","Paris", 1);
        System.out.println("Add route: Paris -> Lisbon (1)");
        addRoute("Paris","Lisbon", 1);

    }
}