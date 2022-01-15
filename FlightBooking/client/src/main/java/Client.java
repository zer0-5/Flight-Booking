import airport.PossiblePath;
import airport.Reservation;
import airport.Route;
import connection.TaggedConnection;
import exceptions.AlreadyLoggedInException;
import exceptions.NotLoggedInException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import static connection.TaggedConnection.Frame;
import static java.lang.System.out;
import static request.RequestType.*;

public class Client implements Runnable {
    private static final Logger logger = LogManager.getLogger(Client.class);

    private static final String host = "localhost";
    private static final int PORT = 12345; // TODO: Mudar isto dps!

    protected final TaggedConnection taggedConnection;
    private final Scanner in; // From console
    private boolean logged_in;

    public Client() throws IOException {
        this.taggedConnection = new TaggedConnection(new Socket(host, PORT)); // TODO: Repetir a conexão caso o server não esteja ligado.
        this.in = new Scanner(System.in);
        this.logged_in = false;
    }

    public void run() {
        try {
            boolean quit = false;
            while (!quit) {
                try {
                    out.print(getMenu());
                    int option = Integer.parseInt(in.nextLine());
                    switch (getRequestType(option)) {
                        case REGISTER -> register();
                        case LOGIN -> loginIO();
                        case EXIT -> {
                            quit();
                            quit = true;
                        }

                        case CANCEL_DAY -> cancelDayIO();
                        case INSERT_ROUTE -> insertRouteIO();

                        case GET_ROUTES -> getRoutes();
                        case GET_RESERVATIONS -> getReservations();
                        case GET_PATHS_BETWEEN -> getPathsBetweenIO();
                        case RESERVE -> reserveIO();
                        case CANCEL_RESERVATION -> cancelReservationIO();
                    }
                    out.println();
                } catch (Exception e) {
                    out.println(e.getMessage() + '\n');
                }
            }

            taggedConnection.close();
        } catch (NotLoggedInException e) {
            out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReservations() throws IOException {
        taggedConnection.send(GET_RESERVATIONS.ordinal(), new ArrayList<>());

        Frame response = taggedConnection.receive();

        out.println("Reservations: ");
        response.data().stream().map(Reservation::deserialize).forEach(out::println);
    }

    public void quit() throws IOException {
        taggedConnection.send(EXIT.ordinal(), new ArrayList<>());
    }

    private void loginIO() throws AlreadyLoggedInException, IOException {
        if (logged_in) throw new AlreadyLoggedInException();

        out.print("Insert username: ");
        String username = in.nextLine();
        out.print("Insert password: ");
        String password = in.nextLine();
        login(username, password);
    }

    /**
     * Sends one request to the server with the username and password.
     * Then, it waits for a response, and handles it.
     */
    public void login(String username, String password) throws IOException {
        List<byte[]> args = new ArrayList<>();

        args.add(username.getBytes(StandardCharsets.UTF_8));
        args.add(password.getBytes(StandardCharsets.UTF_8));

        taggedConnection.send(LOGIN.ordinal(), args);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else {
            out.println("Logged in!");
            logged_in = true;
        }
    }

    private void cancelReservationIO() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        out.print("Insert the id of the reservation: ");
        String id = in.nextLine();

        cancelReservation(UUID.fromString(id));
    }

    public void cancelReservation(UUID reservationId) throws IOException {
        List<byte[]> list = new ArrayList<>();
        list.add(reservationId.toString().getBytes(StandardCharsets.UTF_8));
        taggedConnection.send(CANCEL_RESERVATION.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else {
            logger.info("Cancel reservation with success!");
            logger.info(Reservation.deserialize(response.data().get(0)));
        }
    }


    private void reserveIO() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        List<String> cities = new ArrayList<>();

        out.print("Insert the number of the cities: "); // TODO: Melhorar a mensagem disto
        int num = Integer.parseInt(in.nextLine());

        for (int i = 0; i < num; i++) {
            out.print("Please insert the next city: ");
            String city = in.nextLine();
            cities.add(city);
        }

        out.print("Insert the start date with the following format \"2007-12-03\": ");
        LocalDate start = LocalDate.parse(in.nextLine());

        out.print("Insert the end date with the following format \"2007-12-03\": ");
        LocalDate end = LocalDate.parse(in.nextLine());

        out.println("Reservation id: " + reserve(cities, start, end));
    }

    public UUID reserve(List<String> cities, LocalDate start, LocalDate end) throws IOException {
        List<byte[]> list = new ArrayList<>(cities.stream().map(str -> str.getBytes(StandardCharsets.UTF_8)).toList());

        list.add(start.toString().getBytes(StandardCharsets.UTF_8));
        list.add(end.toString().getBytes(StandardCharsets.UTF_8));

        taggedConnection.send(RESERVE.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) logger.info(response);
        else {
            logger.info("\nReserve with success!");
            UUID id = UUID.fromString(new String(response.data().get(0), StandardCharsets.UTF_8));
            logger.info("Reservation id: " + id);
            return id;
        }
        return null;
    }

    protected void getRoutes() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        List<byte[]> list = new ArrayList<>();
        taggedConnection.send(GET_ROUTES.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else {
            logger.info("Get routes with success!");
            response.data().stream().map(Route::deserialize).forEach(out::println);
        }
    }

    private void getPathsBetweenIO() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        out.print("Insert origin: ");
        String origin = in.nextLine();
        out.print("Insert destination: ");
        String destination = in.nextLine();

        getPathsBetween(origin, destination);

        getPathsBetween(origin, destination).forEach(e -> out.println(e.toStringPretty("")));
    }

    public List<PossiblePath> getPathsBetween(String origin, String destination) throws IOException {
        List<byte[]> args = new ArrayList<>();

        args.add(origin.getBytes(StandardCharsets.UTF_8));
        args.add(destination.getBytes(StandardCharsets.UTF_8));

        taggedConnection.send(GET_PATHS_BETWEEN.ordinal(), args);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else {
            logger.info("Get Possible Path with success!");
            return response.data().stream().map(PossiblePath::deserialize).collect(Collectors.toList());
        }
        return null;
    }

    private void insertRouteIO() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        out.print("Insert origin route: ");
        String origin = in.nextLine();

        out.print("Insert destination route: ");
        String destination = in.nextLine();

        out.print("Insert capacity of the route: ");
        int capacity = Integer.parseInt(in.nextLine());

        insertRoute(origin, destination, capacity);
    }

    public void insertRoute(String origin, String destination, int capacity) throws IOException {
        List<byte[]> list = new ArrayList<>();

        list.add(origin.getBytes(StandardCharsets.UTF_8));
        list.add(destination.getBytes(StandardCharsets.UTF_8));
        list.add(ByteBuffer.allocate(Integer.BYTES).putInt(capacity).array());

        taggedConnection.send(INSERT_ROUTE.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else logger.info("Route successfully inserted!");
    }

    private void cancelDayIO() throws NotLoggedInException, IOException {
        if (!logged_in) throw new NotLoggedInException();

        out.print("Insert the end date with the following format \"2007-12-03\": ");
        LocalDate day = LocalDate.parse(in.nextLine());

        cancelDay(day);
    }

    public void cancelDay(LocalDate day) throws IOException {
        List<byte[]> list = new ArrayList<>();

        list.add(day.toString().getBytes(StandardCharsets.UTF_8));

        taggedConnection.send(CANCEL_DAY.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else logger.info("Day successfully cancelled!");
    }

    protected void register() throws IOException, AlreadyLoggedInException {
        if (logged_in) throw new AlreadyLoggedInException();

        out.print("Insert username: ");
        String username = in.nextLine();

        out.print("Insert password: ");
        String password = in.nextLine();

        List<byte[]> list = new ArrayList<>();
        list.add(username.getBytes(StandardCharsets.UTF_8));
        list.add(password.getBytes(StandardCharsets.UTF_8));

        taggedConnection.send(REGISTER.ordinal(), list);

        Frame response = taggedConnection.receive();

        if (checkError(response)) printError(response);
        else logger.info("Account successfully registered!");
    }

    protected boolean checkError(Frame frame) {
        return new String(frame.data().get(0)).equals("ERROR");
    }

    private void printError(Frame frame) {
        out.println();
        for (int i = 1; i < frame.data().size(); i++) {
            out.println(new String(frame.data().get(i)));
        }
        // out.println("Error with the request: " + frame.data().stream().map(String::new).collect(Collectors.joining(" ")));
    }

}