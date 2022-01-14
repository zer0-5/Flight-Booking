package users;

import airport.Reservation;
import encryption.BCrypt;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
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
     * Set of the reservations of the client
     */
    private final Set<UUID> reservations;
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
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.username = username;
        this.reservations = new HashSet<>();
    }

    /**
     * Checks if the password given is valid to this user.
     *
     * @param password Password to check.
     * @return Is password is correct.
     */
    public boolean validPassword(String password) {
        return BCrypt.checkpw(password, this.password);
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
     * Change password of a user.
     *
     * @param newPassword new password.
     */
    public void changerUserPassword(String newPassword) {
        this.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
    }

    public void addReservation(UUID reservation) {
        this.reservations.add(reservation);
    }

    public void removeReservation(UUID reservation) {
        this.reservations.remove(reservation);
    }

    public boolean containsReservation(UUID reservation) {
        return this.reservations.contains(reservation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) &&
                password.equals(user.password);
    }

    public byte[] serialize() {
        var username = this.username.getBytes(StandardCharsets.UTF_8);
        // var reservations = this.reservations;
        // TODO: Falta implementar aqui a serialização das reservations!
        var password = this.password.getBytes(StandardCharsets.UTF_8);

        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES + username.length + Integer.BYTES + password.length);
        bb.putInt(username.length);
        bb.put(username);
        bb.putInt(password.length);
        bb.put(password);

        return bb.array();
    }


    public static User deserialize(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte[] username = new byte[bb.getInt()];
        bb.get(username);

        byte[] password = new byte[bb.getInt()];
        bb.get(password);

        // TODO: Mudar isto para dar para o Admin tbm
        return new Client(new String(username, StandardCharsets.UTF_8), new String(password, StandardCharsets.UTF_8));
    }
}
