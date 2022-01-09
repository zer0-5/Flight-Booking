package users;

import encryption.BCrypt;

import java.util.Objects;
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
    private String password;

    /**
     * Constructor
     * @param username the username.
     * @param password the password.
     */
    public User(String username, String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.username = username;
    }

    /**
     * Checks if the password given is valid to this user.
     * @param password Password to check.
     * @return Is password is correct.
     */
    public boolean validPassword(String password) {
        return BCrypt.checkpw(password,this.password);
    }

    /**
     * Get the username of this user.
     * @return Username of this object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Change password of a user.
     *
     * @param newPassword new password.
     */
    public void changerUserPassword(String newPassword){
        this.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
