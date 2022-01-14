package server;

import airport.Route;
import connection.TaggedConnection;
import exceptions.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.RequestType;
import system.IAirportSystem;
import users.User;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static request.RequestType.*;


public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final IAirportSystem airportSystem;
    private final TaggedConnection taggedConnection;
    private User account;

    public ClientHandler(Socket socket, IAirportSystem airportSystem) throws IOException {
        this.taggedConnection = new TaggedConnection(socket);
        this.account = null;
        this.airportSystem = airportSystem;
    }

    @Override
    public void run() {
        try {
            logger.info("Starting a new connection with client!");

            boolean quit = false;
            while (!quit) {
                TaggedConnection.Frame frame = taggedConnection.receive();
                logger.debug("Request data: " + frame.tag() + " " + frame.data());

                List<byte[]> data = frame.data();
                try {
                    switch (RequestType.getRequestType(frame.tag())) {
                        case REGISTER -> register(data);
                        case LOGIN -> login(data);
                        case EXIT -> quit = true;

                        case CANCEL_DAY -> cancelDay(data);
                        case INSERT_ROUTE -> insertRoute(data);

                        case GET_ROUTES -> getRoutes(data);
                        case RESERVE -> reserve(data);
                        case CANCEL_RESERVATION -> cancelReservation(data);
                    }

                    logger.info("Request with type " + RequestType.getRequestType(frame.tag()) + " has been successfully handled!");

                } catch (UserNotFoundException | InvalidCredentialsException | AlreadyLoggedInException e) {
                    List<byte[]> list = new ArrayList<>();
                    list.add("ERROR".getBytes(StandardCharsets.UTF_8));
                    if (e.getMessage() != null) list.add(e.getMessage().getBytes(StandardCharsets.UTF_8));

                    logger.info("Request with type " + RequestType.getRequestType(frame.tag()) + " has result in a error: " + e.getMessage());

                    taggedConnection.send(frame.tag(), list);
                }
            }
            taggedConnection.close();
            logger.info("Connection between client closed!");
        } catch (IOException e) {
            logger.info("Something went wrong with the connection!");
            // e.printStackTrace();
        } catch (Exception e) {
            logger.info("Error closing the connection!");
        }

    }

    private void cancelReservation(List<byte[]> data) throws ReservationNotFoundException,
            ReservationDoesNotBelongToTheClientException, UserNotFoundException, IOException {

        List<byte[]> list = new ArrayList<>();
        list.add(airportSystem.cancelReservation(account.getUsername(), UUID.fromString(new String(data.get(0)))).serialize());
        sendOk(CANCEL_RESERVATION.ordinal(), list);
    }

    private void reserve(List<byte[]> data) {
        //airportSystem.reserveFlight(UUID.fromString(new String(data.get(0), StandardCharsets.UTF_8)), data.get(1), data.get(2), data.get(3));
        // TODO:
    }

    private void getRoutes(List<byte[]> data) throws IOException {
        sendOk(GET_ROUTES.ordinal(), airportSystem.getRoutes().stream().map(Route::serialize).collect(Collectors.toList()));
        //airportSystem.getRoutes();
        // TODO:
    }

    private void insertRoute(List<byte[]> data) throws RouteDoesntExistException, RouteAlreadyExistsException, IOException {
        airportSystem.addRoute(new String(data.get(0)), new String(data.get(1)), ByteBuffer.wrap(data.get(2)).getInt());
        sendOk(INSERT_ROUTE.ordinal(), new ArrayList<>());
    }

    private void cancelDay(List<byte[]> data) {
        // TODO:
        //airportSystem.cancelDay()
    }

    private void register(List<byte[]> data) throws UsernameAlreadyExistsException, IOException, AlreadyLoggedInException {
        if (isLoggedIn()) throw new AlreadyLoggedInException(account);
        airportSystem.registerClient(new String(data.get(0)), new String(data.get(1)));
        sendOk(REGISTER.ordinal(), new ArrayList<>());
    }

    public boolean isLoggedIn() {
        return account != null;
    }

    private void login(List<byte[]> data) throws UserNotFoundException, InvalidCredentialsException, AlreadyLoggedInException, IOException {
        if (isLoggedIn()) throw new AlreadyLoggedInException(account);
        this.account = airportSystem.authenticate(new String(data.get(0)), new String(data.get(1)));
        sendOk(LOGIN.ordinal(), new ArrayList<>());
    }

    private void sendOk(int type, List<byte[]> args) throws IOException {
        if (args.size() == 0) args.add("Ok".getBytes(StandardCharsets.UTF_8));
        taggedConnection.send(type, args);
    }

}