package airport;

import users.User;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Represents the reservation of one or multiple flights.
 */
public class Reservation {

    /**
     * Reservation code or id of this reservation.
     */
    public final UUID id;

    /**
     * Client that owns this reservation.
     */
    private final User client;

    /**
     * Flights with the connections of the reservation.
     * E.g. Lisbon -> Tokyo -> London
     */
    private final Set<Flight> flights;

    // TODO: Não é preciso ReadWrite lock aqui!!!
    private final Lock readLockFlights;
    private final Lock writeLockFlights;

    /**
     * Constructor
     *
     * @param client     Client.
     * @param flightsIds a set of flight's id.
     */
    public Reservation(User client, Set<Flight> flightsIds) {
        this.id = UUID.randomUUID();
        this.client = client;
        this.flights = flightsIds;
        ReentrantReadWriteLock rwFlights = new ReentrantReadWriteLock();
        this.readLockFlights = rwFlights.readLock();
        this.writeLockFlights = rwFlights.writeLock();
    }

    public Reservation(UUID id, User client, Set<Flight> flights) {
        this.id = id;
        this.client = client;
        this.flights = flights;
        ReentrantReadWriteLock rwFlights = new ReentrantReadWriteLock();
        this.readLockFlights = rwFlights.readLock();
        this.writeLockFlights = rwFlights.writeLock();
    }

    public Reservation(UUID uuid) {
        this.id = uuid;
        this.client = null;
        this.flights = null;
        this.readLockFlights = null;
        this.writeLockFlights = null;
    }

    public Reservation(UUID id, String username, Set<Flight> flights) {
        this.id = id;
        this.client = new User(username);
        this.flights = new HashSet<>(flights);
        ReentrantReadWriteLock rwFlights = new ReentrantReadWriteLock();
        this.readLockFlights = rwFlights.readLock();
        this.writeLockFlights = rwFlights.writeLock();
    }

    public User getClient() {
        return client;
    }

    public Set<Flight> getFlights() {
        return new HashSet<>(flights);
    }

    /**
     * Cancel the reservation on all flights involved in the given reservation
     *
     */
    public void cancelReservation() {
        try {
            writeLockFlights.lock();
            for (Flight flight : flights) {
                if (flight != null)
                    flight.removeReservation(this);
            }
        } finally {
            writeLockFlights.unlock();
        }
    }

    /**
     * Checks if the given user made the reservation
     *
     * @param user User
     * @return true if are the same user
     */
    public boolean checksUser(User user) {
        return client.equals(user);
    }

    public byte[] serialize() {
        var uuid = id.toString().getBytes(StandardCharsets.UTF_8);
        byte[] user = client.getUsername().getBytes(StandardCharsets.UTF_8);
        var flights = this.flights.stream().map(Flight::serialize).collect(Collectors.toSet());
        ByteBuffer bb = ByteBuffer.allocate(
                Integer.BYTES + uuid.length +
                Integer.BYTES + user.length +
                Integer.BYTES + flights.stream().mapToInt(arr -> arr.length).sum()
        );

        bb.putInt(uuid.length);
        bb.put(uuid);

        bb.putInt(user.length);
        bb.put(user);

        bb.putInt(flights.size());
        for (byte[] flight : flights) {
            bb.put(flight); // Flight
        }

        return bb.array();
    }

    public static Reservation deserialize(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte[] idB = new byte[bb.getInt()];
        bb.get(idB);
        var id = UUID.fromString(new String(idB, StandardCharsets.UTF_8));

        byte[] usernameB = new byte[bb.getInt()];
        bb.get(usernameB);
        String username = new String(usernameB, StandardCharsets.UTF_8);

        int size = bb.getInt();
        Set<Flight> flights = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            Flight flight = Flight.deserialize(bb);
            flights.add(flight);
        }
        return new Reservation(id, username, flights);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id.equals(that.id) && client.equals(that.client) && flights.equals(that.flights);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Flight id=").append(id);
        stringBuilder.append(" user_username=").append(client.getUsername());
        stringBuilder.append(" reservation flights=");
        Iterator<Flight> flights = this.flights.iterator();

        for (int i = 0; i < this.flights.size() - 1; i++) {
            stringBuilder.append("[").append(flights.next()).append("] -> ");
        }
        stringBuilder.append("[").append(flights.next()).append("]");

        return stringBuilder.toString();
    }
}
