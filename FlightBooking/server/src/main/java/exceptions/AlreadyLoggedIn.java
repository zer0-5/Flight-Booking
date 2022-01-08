package exceptions;

import users.User;

public class AlreadyLoggedIn extends Exception {
    public AlreadyLoggedIn(User account) {
        super("User with username: " + account.getUsername() + " is already logged in!");
    }
}
