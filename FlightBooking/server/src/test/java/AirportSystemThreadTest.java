import exceptions.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


class AirportSystemThreadTest {

    // Used to test
    private final String username = "admin";
    private AirportSystem airportSystem;
    // Used to test.
    private LocalDate date;

    @BeforeAll
    static void startTest() {
        System.out.println("Starting test with threads");
    }

    @AfterAll
    static void endTest() {
        System.out.println("Ending test");
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        airportSystem = new AirportSystem();
        date = LocalDate.now();
        //System.out.println("---- TEST ----");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        //System.out.println("---------------");
    }

    /**
     * Private method to initialize airport system.
     * Capacity of each flight is 1.
     */
    private void initRoutes_LondonParisLisbon()  {
        //            System.out.println(airportSystem.getReservation(reservation).toString());
        try {
            airportSystem.addRoute("London", "Paris", 3);
            airportSystem.addRoute("Paris", "Lisbon", 3);
        } catch (RouteAlreadyExistsException e) {
            e.printStackTrace();
        } catch (RouteDoesntExistException e) {
            e.printStackTrace();
        }

    }

    private void initUser() {
        try {
            airportSystem.registerAdmin(username, username);
        } catch (UsernameAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    private void initUserAndRoutes_LondonParisLisbon() {
        initUser();
        initRoutes_LondonParisLisbon();
    }


    // -------------------- Add Route -------------------
    @org.junit.jupiter.api.Test
    void testMultipleReservations(){
        initUserAndRoutes_LondonParisLisbon();
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++){
            threads[i] = new Thread(new makeReservation(airportSystem));
        }
        for (Thread t : threads){
            t.start();
        }
        try {
            for (Thread t : threads){
                t.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class makeReservation implements Runnable{
        private AirportSystem airportSystem;

        public makeReservation(AirportSystem airportSystem) {
            this.airportSystem = airportSystem;
        }

        public void run() {
            System.out.println("Uma thread passou aqui");
            List<String> cities1 = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
            try {

                airportSystem.reserveFlight("username", cities1, date, date);
                airportSystem.reserveFlight("username", cities1, date, date);
            } catch (BookingFlightsNotPossibleException e) {
                e.printStackTrace();
            } catch (RouteDoesntExistException e) {
                e.printStackTrace();
            }

        }
    }


}