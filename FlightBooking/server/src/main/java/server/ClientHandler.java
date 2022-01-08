package server;

import connection.TaggedConnection;
import exceptions.AlreadyLoggedInException;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import request.RequestType;
import system.IAirportSystem;
import users.User;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static request.RequestType.LOGIN;


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
            TaggedConnection.Frame frame = taggedConnection.receive();
            List<byte[]> data = frame.data();

            logger.debug("Request data: " + frame.tag() + " " + frame.data());
            boolean quit = false;
            while (!quit) {
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

    private void cancelReservation(List<byte[]> data) {
        // TODO:
    }

    private void reserve(List<byte[]> data) {
        // TODO:
    }

    private void getRoutes(List<byte[]> data) {
        // TODO:
    }

    private void insertRoute(List<byte[]> data) {
        // TODO:
    }

    private void cancelDay(List<byte[]> data) {
        // TODO:
    }

    private void register(List<byte[]> data) {
        // TODO: airportSystem.register(data.get(0));
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
        args.add(0, "Ok".getBytes(StandardCharsets.UTF_8));
        taggedConnection.send(type, args);
    }

}
