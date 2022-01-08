package server;

import exceptions.AlreadyLoggedIn;
import exceptions.InvalidCredentialsException;
import exceptions.UserNotFoundException;
import request.RequestType;
import system.IAirportSystem;
import users.User;

import java.io.IOException;
import java.net.Socket;
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
            boolean quit = false;
            while (!quit) {
                TaggedConnection.Frame frame = taggedConnection.receive();
                List<byte[]> data = frame.data();

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

        } catch (IOException | UserNotFoundException | InvalidCredentialsException | AlreadyLoggedIn e) {
            // TODO: Handle the error here
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
}
