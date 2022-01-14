package gui;

import airport.Route;
import connection.TaggedConnection;
import exceptions.NotLoggedInException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static request.RequestType.*;

public class ClientCommunication {
    //private static final Logger logger = LogManager.getLogger(ClientCommunication.class);

    private static final String host = "localhost";
    private static final int PORT = 12345; // TODO: Mudar isto dps!

    private final TaggedConnection taggedConnection;
    private final Scanner in; // From console
    private boolean logged_in;

    public ClientCommunication() throws IOException {
        //this.taggedConnection = null;
        this.taggedConnection = new TaggedConnection(new Socket(host, PORT)); // TODO: Repetir a conexão caso o server não esteja ligado.
        this.in = new Scanner(System.in);
        this.logged_in = false;
    }

    //public void run() {
    //    try {
    //        boolean quit = false;
    //        while (!quit) {
    //            System.out.print(getMenu());
    //            int option = Integer.parseInt(in.nextLine());
    //            switch (getRequestType(option)) {
    //                case REGISTER -> register();
    //                case LOGIN -> login();
    //                case EXIT -> {
    //                    quit();
    //                    quit = true;
    //                }

    //                case CANCEL_DAY -> cancelDay();
    //                case INSERT_ROUTE -> insertRoute();

    //                case GET_ROUTES -> getRoutes();
    //                case RESERVE -> reserve();
    //                case CANCEL_RESERVATION -> cancelReservation();
    //            }
    //            System.out.println();
    //        }

    //        taggedConnection.close();
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //}

    // private void quit() throws IOException {
    //     taggedConnection.send(EXIT.ordinal(), new ArrayList<>());
    // }

    // /**
    //  * Sends one request to the server with the username and password.
    //  * Then, it waits for a response, and handles it.
    //  */
    // private void login() throws IOException {
    //     System.out.println("Insert username: ");
    //     String username = in.nextLine();
    //     System.out.println("Insert password: ");
    //     String password = in.nextLine();

    //     List<byte[]> args = new ArrayList<>();
    //     args.add(username.getBytes(StandardCharsets.UTF_8));
    //     args.add(password.getBytes(StandardCharsets.UTF_8));

    //     taggedConnection.send(LOGIN.ordinal(), args);

    //     TaggedConnection.Frame response = taggedConnection.receive();

    //     if (checkError(response)) printError(response);
    //     else logged_in = true;
    // }

    // private void cancelReservation() {
    //     // TODO:
    // }

    // private void reserve() {
    //     // TODO:
    // }

    // private void getRoutes() throws NotLoggedInException, IOException {
    //     if (!logged_in) throw new NotLoggedInException();

    //     List<byte[]> list = new ArrayList<>();
    //     taggedConnection.send(GET_ROUTES.ordinal(), list);

    //     TaggedConnection.Frame response = taggedConnection.receive();

    //     if (checkError(response)) printError(response);
    //     else {
    //         //logger.info("Get routes with success!");
    //         response.data().stream().map(Route::deserialize).toList().forEach(System.out::println);
    //     }
    //     // TODO:
    // }

    // private void insertRoute() throws IOException, NotLoggedInException {
    //     if (!logged_in) throw new NotLoggedInException();

    //     System.out.println("Insert origin route: ");
    //     String origin = in.nextLine();

    //     System.out.println("Insert destiny route: ");
    //     String destiny = in.nextLine();

    //     System.out.println("Insert capacity of the route: ");
    //     int capacity = Integer.parseInt(in.nextLine());

    //     List<byte[]> list = new ArrayList<>();
    //     list.add(origin.getBytes(StandardCharsets.UTF_8));
    //     list.add(destiny.getBytes(StandardCharsets.UTF_8));
    //     list.add(ByteBuffer.allocate(Integer.BYTES).putInt(capacity).array());
    //     taggedConnection.send(INSERT_ROUTE.ordinal(), list);

    //     TaggedConnection.Frame response = taggedConnection.receive();

    //     if (checkError(response)) printError(response);
    //     else System.out.println("Route successfully inserted!");
    // }

    // private void cancelDay() {
    //     // TODO:
    // }

    // private void register() throws IOException {
    //     System.out.println("Insert username: ");
    //     String username = in.nextLine();

    //     System.out.println("Insert password: ");
    //     String password = in.nextLine();

    //     List<byte[]> list = new ArrayList<>();
    //     list.add(username.getBytes(StandardCharsets.UTF_8));
    //     list.add(password.getBytes(StandardCharsets.UTF_8));

    //     taggedConnection.send(REGISTER.ordinal(), list);

    //     TaggedConnection.Frame response = taggedConnection.receive();

    //     if (checkError(response)) printError(response);
    //     else System.out.println("Account successfully registered!");
    // }

    // private boolean checkError(TaggedConnection.Frame frame) {
    //     return new String(frame.data().get(0)).equals("ERROR");
    // }

    // private void printError(TaggedConnection.Frame frame) {
    //     System.out.println("ERROR!");
    //     for (int i = 1; i < frame.data().size(); i++) {
    //         System.out.println(new String(frame.data().get(i)));
    //     }
    //     System.out.println("Error with the request: " + frame.data().stream().map(String::new).collect(Collectors.joining(" ")));
    // }

}
