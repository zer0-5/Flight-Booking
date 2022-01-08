import connection.TaggedConnection;
import request.RequestType;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static connection.TaggedConnection.Frame;

public class Client implements Runnable {
    // private static final Logger logger = LogManager.getLogger(Client.class);
    private static final String host = "localhost";
    private static final int PORT = 12345; // TODO: Mudar isto dps!

    private final TaggedConnection taggedConnection;
    private final Scanner in; // From console
    private boolean logged_in;

    public Client() throws IOException {
        this.taggedConnection = new TaggedConnection(new Socket(host, PORT));
        this.in = new Scanner(System.in);
        this.logged_in = false;
    }

    public void run() {
        try {
            boolean quit = false;
            while (!quit) {
                System.out.println("Select 1 to login: ");
                int option = Integer.parseInt(in.nextLine());
                switch (RequestType.getRequestType(option)) {
                    // case REGISTER ->
                    case LOGIN -> login();
                    case EXIT -> quit = true;
                }
            }

            taggedConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends one request to the server with the username and password.
     * Then, it waits for a reponse, and handles it.
     */
    private void login() {
        try {
            System.out.println("Insert username: ");
            String username = in.nextLine();
            System.out.println("Insert password: ");
            String password = in.nextLine();

            List<byte[]> args = new ArrayList<>();
            args.add(username.getBytes(StandardCharsets.UTF_8));
            args.add(password.getBytes(StandardCharsets.UTF_8));

            taggedConnection.send(RequestType.LOGIN.ordinal(), args);

            Frame response = taggedConnection.receive();

            if (checkError(response)) printError(response);
            else logged_in = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkError(Frame frame) {
        return new String(frame.data().get(0)).equals("ERROR");
    }

    private void printError(Frame frame) {
        System.out.println("ERROR!");
        for (int i = 1; i < frame.data().size(); i++) {
            System.out.println(new String(frame.data().get(i)));
        }
        // TODO: Adicionar o logger aqui
    }

}