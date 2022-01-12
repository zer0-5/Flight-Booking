package users;

/**
 * Admin class.
 */
public class Admin extends User {

    /**
     * Constructors
     *
     * @param username the username.
     * @param password the password.
     */
    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
