package server;

import connection.TaggedConnection;
import exceptions.AlreadyLoggedIn;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotFoundException;
import request.RequestType;
import system.IAirportSystem;
import users.User;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler implements Runnable {
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
            TaggedConnection.Frame frame = taggedConnection.receive();
            List<byte[]> data = frame.data();

            try {
                boolean quit = false;
                while (!quit) {
                    switch (RequestType.values()[frame.tag()]) {
                        case REGISTER -> register(data);
                        case LOGIN -> login(data);
                        case EXIT -> quit = true;

                        case CANCEL_DAY -> cancelDay(data);
                        case INSERT_ROUTE -> insertRoute(data);

                        case GET_ROUTES -> getRoutes(data);
                        case RESERVE -> reserve(data);
                        case CANCEL_RESERVATION -> cancelReservation(data);
                    }
                }

            } catch (UserNotFoundException | InvalidCredentialsException | AlreadyLoggedIn e) {
                List<byte[]> list = new ArrayList<>();
                list.add("ERROR".getBytes(StandardCharsets.UTF_8));
                if (e.getMessage() != null) list.add(e.getMessage().getBytes(StandardCharsets.UTF_8));
                taggedConnection.send(frame.tag(), list);
            }

        } catch (IOException e) {
            e.printStackTrace();
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

    private void login(List<byte[]> data) throws UserNotFoundException, InvalidCredentialsException, AlreadyLoggedIn {
        if (isLoggedIn()) throw new AlreadyLoggedIn(account);
        this.account = airportSystem.authenticate(new String(data.get(0)), new String(data.get(1)));
    }

    private void sendOk(int type) throws IOException {
        List<byte[]> list = new ArrayList<>();
        list.add("Ok".getBytes(StandardCharsets.UTF_8));
        taggedConnection.send(type, list);
    }

}
