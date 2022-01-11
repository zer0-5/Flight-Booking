package server;


import exceptions.RouteAlreadyExistsException;
import exceptions.RouteDoesntExistException;
import exceptions.UsernameAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import system.AirportSystem;
import system.IAirportSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;

public class Main {
    public static final int PORT = 12345;
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final int NTHREADS = 50;
    @SuppressWarnings({"CanBeFinal", "FieldMayBeFinal", "FieldCanBeLocal"})
    private static boolean running = true;

    public static void main(String[] args) throws IOException, UsernameAlreadyExistsException, RouteDoesntExistException, RouteAlreadyExistsException {
        IAirportSystem iAirportSystem = new AirportSystem();

        iAirportSystem.registerAdmin("admin", "admin");
        iAirportSystem.registerClient("1", "1");
        iAirportSystem.addRoute("Porto", "Lisbon", 200);


        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("ServerSocket starting...");
            var pool = Executors.newFixedThreadPool(NTHREADS); // TODO: Fazer isto manualmente

            while (running) pool.execute(new ClientHandler(serverSocket.accept(), iAirportSystem));
        }
        logger.info("ServerSocket closing...");
    }

}
