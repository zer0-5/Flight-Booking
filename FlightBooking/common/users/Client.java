package users;

import java.util.HashSet;
import java.util.Set;

/**
 * Client class.
 */
public class Client extends User {

    /**
     * Set of the current notifications of the client.
     */
    private final Set<String> notifications;

    /**
     * Constructor
     * @param username the username.
     * @param password the password.
     */
    public Client(String username, String password) {
        super(username, password);
        this.notifications = new HashSet<>();
    }
}
