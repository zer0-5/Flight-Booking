package users;

/**
 * User class.
 */
public abstract class User {

    /**
     * Id.
     */
    private final UUID id;

    /**
     * Username.
     */
    private final String username;

    /**
     * Password.
     */
    private String password;

    /**
     * Constructor
     *
     * @param username the username.
     * @param password the password.
     */
    public User(String username, String password) {
        this.id = UUID.randomUUID();
        this.password = password;
        this.username = username;
    }

    /**
     * Checks if the password given is valid to this user.
     *
     * @param password Password to check.
     * @return Is password is correct.
     */
    public boolean validPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Get the username of this user.
     *
     * @return Username of this object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the id fo this user.
     * @return ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Change password of a user.
     *
     * @param newPassword new password.
     */
    public void changerUserPassword(String newPassword){
        this.password = newPassword;
    }
}
