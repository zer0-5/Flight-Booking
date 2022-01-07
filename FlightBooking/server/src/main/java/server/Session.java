package server;

import java.util.ArrayList;

public class Session {
    /**
     * Message history.
     */
    // private final List<Message> requestHistory;
    // TODO: loggedIn change to the account of the login
    private final String account;

    public Session() {
        this.account = null;
        // requestHistory = new ArrayList<>();
    }

    /**
    public boolean handle(Message message) {
        if (message instanceof Quit) return false;

        if (isLoggedIn() && message instanceof Login) ; // message.handle();

        requestHistory.add(message);

        return true;
    }
     */

    public boolean isLoggedIn() {
        return account != null;
    }
}

