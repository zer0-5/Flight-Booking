package airport;

import users.User;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
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
    private final Set<UUID> flightIds;

    /**
     * Constructor
     *
     * @param client     Client.
     * @param flightsIds a set of flight's id.
     */
    public Reservation(User client, Set<UUID> flightsIds) {
        this.id = UUID.randomUUID();
        this.client = client;
        this.flights = flightsIds;
    }

    private Reservation(UUID id, User client, Set<Flight> flights) {
        this.id = id;
        this.client = client;
        this.flights = flights;
    }

    /**
     * Return all flight ids from this reservation.
     *
     * @return Flight ids
     */
    public Set<UUID> getFlightIds() {
        return new HashSet<>(flightIds);
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
        var user = client.serialize();
        var flights = this.flights.stream().map(Flight::serialize).collect(Collectors.toSet());
        ByteBuffer bb = ByteBuffer.allocate(
                Integer.BYTES + uuid.length +
                Integer.BYTES + user.length +
                Integer.BYTES + Integer.BYTES * flights.size() + flights.stream().mapToInt(arr -> arr.length).sum()
        );

        bb.putInt(uuid.length);
        bb.put(uuid);

        bb.put(user);

        bb.putInt(flights.size());
        for (byte[] flight : flights) {
            bb.putInt(flight.length); // Flight size
            bb.put(flight); // Flight
        }

        return bb.array();
    }

    public static Reservation deserialize(byte[] bytes) {
        // TODO: Alterar isto de Flight para Reservation!
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte[] idB = new byte[bb.getInt()];
        bb.get(idB);

        var arr = bb.array();
        var client = User.deserialize(arr);

        Set<Flight> flights = null;

        return new Reservation(UUID.fromString(new String(idB, StandardCharsets.UTF_8)), client, flights);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Reservation flights: ");
        Iterator<Flight> flights = this.flights.iterator();

        for (int i = 0; i < this.flights.size() - 1; i++) {
            stringBuilder.append(flights.next()).append("->");
        }
        stringBuilder.append(flights.next());

        return stringBuilder.toString();
    }
}
