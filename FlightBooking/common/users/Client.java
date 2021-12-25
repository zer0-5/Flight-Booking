package common.users;

import java.util.HashSet;
import java.util.Set;

public class Client extends User {

    private final Set<String> notifications;

    public Client(String username, String password) {
        super(username, password);
        this.notifications = new HashSet<>();
    }
}
