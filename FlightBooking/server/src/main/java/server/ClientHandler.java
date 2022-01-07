package server;

import request.RequestType;
import system.IAirportSystem;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static server.TaggedConnection.*;


public class ClientHandler implements Runnable {
    private final Session session;
    private IAirportSystem airportSystem;
    private final TaggedConnection taggedConnection;

    public ClientHandler(Socket socket, IAirportSystem airportSystem) throws IOException {
        this.taggedConnection = new TaggedConnection(socket);
        this.airportSystem = airportSystem;
        this.session = new Session();
    }

    @Override
    public void run() {
        try {
            boolean quit = false;
            while (!quit) {
                Frame frame = taggedConnection.receive();
                List<byte[]> data = frame.data();
                switch (RequestType.values()[frame.tag()]) {
                    case LOGIN -> login(new String(data.get(0)), new String(data.get(1)));
                    case EXIT -> quit = true;
                }

                //quit = session.handle(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login(String name, String password) {
        boolean valid = airportSystem.
    }
}
