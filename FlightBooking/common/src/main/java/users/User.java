package users;

import java.util.UUID;

/**
 * User class.
 */
public abstract class User {

    /**
     * Username.
     */
    private final String username;

    /**
     * Password.
     */
    private final String password;

    /**
     * Constructor
     * @param username the username.
     * @param password the password.
     */
    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    /**
     * Checks if the password given is valid to this user.
     * @param password Password to check.
     * @return Is password is correct.
     */
    public boolean validPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Get the username of this user.
     * @return Username of this object.
     */
    public String getUsername() {
        return username;
    }
}
