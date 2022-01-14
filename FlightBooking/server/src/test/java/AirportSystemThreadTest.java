import airport.Reservation;
import exceptions.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


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
            airportSystem.addRoute("London", "Paris", 2);
            airportSystem.addRoute("Paris", "Lisbon", 3);
        } catch (RouteAlreadyExistsException e) {
            e.printStackTrace();
        } catch (RouteDoesntExistException e) {
            e.printStackTrace();
        }
    }

    /**
     * Private method to initialize airport system.
     * Capacity of each flight is 1.
     */
    private void initRoutes_LondonParisLisbon(int routeCapacity)  {
        //            System.out.println(airportSystem.getReservation(reservation).toString());
        try {
            airportSystem.addRoute("London", "Paris", routeCapacity);
            airportSystem.addRoute("Paris", "Lisbon", routeCapacity);
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


    // -------------------- Multiple reservations Route -------------------
    @org.junit.jupiter.api.Test
    void testMultipleReservations(){
        int N = 20;
        int numberDays = 3;
        int routeCapacity = 2;
        initUser();
        initRoutes_LondonParisLisbon(routeCapacity);
        Thread[] threads = new Thread[N];
        MakeReservation makeReservation = new MakeReservation(airportSystem, numberDays, new TreeSet<>());

        for (int i = 0; i < N; i++){
            threads[i] = new Thread(makeReservation);
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
        assert makeReservation.reservationSucceed == routeCapacity * numberDays;
    }

    private class MakeReservation implements Runnable {
        private AirportSystem airportSystem;

        protected int reservationSucceed;
        protected int numberDays;
        private ReentrantLock lock;
        protected Set<UUID> reserves;

        public MakeReservation(AirportSystem airportSystem, int numberDays, Set<UUID> reservations) {
            this.airportSystem = airportSystem;
            this.reservationSucceed = 0;
            this.numberDays = numberDays;
            lock = new ReentrantLock();
            this.reserves = reservations;
        }

        public void success(UUID reservationCode) {
            try {
                lock.lock();
                reservationSucceed++;
                reserves.add(reservationCode);
            } finally {
                lock.unlock();
            }
        }

        public void run() {
            List<String> cities1 = new ArrayList<>(Arrays.asList("Paris", "Lisbon"));
            try {
                UUID id = airportSystem.reserveFlight(username, cities1, date, date.plusDays(numberDays - 1));
                System.out.println("Reserva voo");
                success(id);
            } catch (Exception e) {
            }
        }
    }

    // ------------------ Teste cancel day ----------------------

    /**
     * Reserves flights and dele days at a random order.
     */
    @org.junit.jupiter.api.Test
    void testCancelDayAndReservations(){
        System.out.println("Boa sorte");
        int Nreservations = 20;
        int numberDays = 2;
        int routeCapacity = 5;
        initUser();
        initRoutes_LondonParisLisbon(routeCapacity);
        List<Thread> threads = new ArrayList<Thread>();
        MakeReservation makeReservation = new MakeReservation(airportSystem, numberDays, new TreeSet<>());

        for (int i = 0; i < Nreservations; i++){
            threads.add(new Thread(makeReservation));
        }
        for (int i = 0; i < numberDays; i++){
            threads.add(new Thread(new CancelDay(airportSystem, i)));
        }
        Collections.shuffle(threads);
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

    private class CancelDay implements Runnable {
        private AirportSystem airportSystem;

        protected int dayToBeCanceled;

        public CancelDay(AirportSystem airportSystem, int dayToBeCanceled) {
            this.airportSystem = airportSystem;
            this.dayToBeCanceled = dayToBeCanceled;
        }

        public void run() {
            try {
                System.out.println("Cancela dia " + date.plusDays(dayToBeCanceled));
                airportSystem.cancelDay(date.plusDays(dayToBeCanceled));
                System.out.println("Dia cancelado" + date.plusDays(dayToBeCanceled));
            } catch (DayAlreadyCanceledException e){
                System.out.println("Dia já cancelado");
            }

        }
    }

    //--------------Tenta cancelar reservas após dias cancelados -----
    //Deve ter 0 reservas canceladas com sucesso
    /**
     * Reserves flights and dele days at a random order.
     */
    @org.junit.jupiter.api.Test
    void testCancelReservationAfterCancelDay(){
        System.out.println("Boa sorte outra vez");
        int Nreservations = 20;
        int numberDays = 2;
        int routeCapacity = 5;
        initUser();
        initRoutes_LondonParisLisbon(routeCapacity);
        List<Thread> threads = new ArrayList<Thread>();
        Set<UUID> reservas = new TreeSet<>();
        MakeReservation makeReservation = new MakeReservation(airportSystem, numberDays, reservas);
        List<Thread> threadsCancelFlights = new ArrayList<Thread>();

        for (int i = 0; i < Nreservations; i++){
            threads.add(new Thread(makeReservation));
        }
        for (int i = 0; i < numberDays; i++){
            threads.add(new Thread(new CancelDay(airportSystem, i)));
        }
        Collections.shuffle(threads);
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
        for (UUID reservation : reservas){
            threadsCancelFlights.add(new Thread(new CancelFlight(airportSystem, reservation)));
        }
        for (Thread toCancel : threadsCancelFlights)
            toCancel.start();
        try {
            for (Thread t : threadsCancelFlights){
                t.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


    }




    private class CancelFlight implements Runnable {
        private AirportSystem airportSystem;
        private ReentrantLock lock;
        protected int numberCancelations;
        protected UUID toCancel;

        public CancelFlight(AirportSystem airportSystem, UUID toCancel) {
            this.airportSystem = airportSystem;
            this.toCancel = toCancel;
        }
        public void success() {
            try {
                lock.lock();
                numberCancelations++;
            } finally {
                lock.unlock();
            }
        }

        public void run() {
            try {
                System.out.println("Cancela voo");
                airportSystem.cancelReservation(username, toCancel);
            } catch (Exception e) {
                //É suposto nenhuma reserva ser cancelada,
                // porque todas são inválidas, visto que os dias foram cancelados.
                success();
            }
        }
    }

}

