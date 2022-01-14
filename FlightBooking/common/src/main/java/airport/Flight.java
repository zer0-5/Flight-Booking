package airport;

import exceptions.FullFlightException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a flight.
 */
public class Flight {

    /**
     * Id of the flight.
     */
    public final UUID id;

    /**
     * Route of the flight.
     */
    public final Route route;

    /**
     * Date of the flight.
     */
    public final LocalDate date;

    /**
     * Association between each reservation code to the flight.
     * <p>
     * This is necessary because when a flight is canceled, we need
     * to know all reservations associated to cancel then.
     * Like, if a connection flight is canceled, we need to cancel the flights associated to that connection
     */
    private final Set<UUID> reservations;

    private int currentOccupation;

    public Flight(UUID id, Route route, LocalDate date, Set<UUID> reservations) {
        this.id = id;
        this.route = route;
        this.date = date;
        this.reservations = reservations;
    }

    /**
     * Constructor of the flight.
     *
     * @param route the route.
     * @param date  the date.
     */
    public Flight(Route route, LocalDate date) {
        this.currentOccupation = 0;
        this.id = UUID.randomUUID();
        this.route = route;
        this.date = date;
        this.reservations = new HashSet<>();
    }

    /**
     * Adds a reservation to this flight.
     *
     * @param reservationId the id of the reservation.
     * @return true if this set did not already contain the specified element.
     * @throws FullFlightException is launched if aren't seats available.
     */
    public boolean addReservation(UUID reservationId) throws FullFlightException {
        if (route.capacity > reservations.size())
            return this.reservations.add(reservationId);
        throw new FullFlightException();
    }

    /**
     * Removes a reservation to this flight.
     *
     * @param reservationId the id of the reservation.
     * @return true if this set contained the specified element.
     */
    public boolean removeReservation(UUID reservationId) {
        cancelSeat();
        return this.reservations.remove(reservationId);
    }

    /**
     * Get all reservation's ids on the flight
     *
     * @return reservation's ids
     */
    public Set<UUID> getReservations() {
        return new HashSet<>(reservations);
    }

    /**
     * Checks if there are available seats.
     *
     * @return true if there is a seat.
     */
    public boolean seatAvailable() {
        return route.capacity > currentOccupation;
    }

    public void preReservationSeat() throws FullFlightException {
        if (seatAvailable())
            currentOccupation++;
        else
            throw new FullFlightException();
    }

    public void cancelSeat() {
        if (currentOccupation > 0)
            currentOccupation--;
    }


    public Flight deserialize(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        byte[] id = new byte[bb.getInt()];
        bb.get(id);

        Route route = Route.deserialize(bytes);

        byte[] dateBuffer = new byte[bb.getInt()];
        bb.get(dateBuffer);
        String date = new String(dateBuffer);

        int size = bb.getInt();
        Set<UUID> reservations = new HashSet<>(size);
        for (int i=0; i < size; i++) {
            byte[] reservationId = new byte[bb.getInt()];
            bb.get(reservationId);

            reservations.add(UUID.fromString(new String(reservationId)));
        }
        return new Flight(UUID.fromString(new String(id)) ,route, LocalDate.parse(date), reservations);
    }

    public byte[] serialize() {
        byte[] id = this.id.toString().getBytes(StandardCharsets.UTF_8);
        byte[] route = this.route.serialize();
        String date = this.date.toString();
        ByteBuffer bb = ByteBuffer.allocate(route.length + Integer.BYTES + date.length());

        bb.putInt(id.length);
        bb.put(id);
        bb.putInt(route.length);
        bb.put(route);
        bb.putInt(date.length());
        bb.put(date.getBytes(StandardCharsets.UTF_8));

        bb.putInt(this.reservations.size());
        for ( UUID reservationId : this.reservations) {
            byte[] reservationIdByte = reservationId.toString().getBytes(StandardCharsets.UTF_8);
            bb.putInt(reservationIdByte.length);
            bb.put(reservationIdByte);
        }

        return bb.array();
    }

    @Override
    public String toString() {
        return "Flight: " + "route=" + route +
                " date=" + date;
    }
}
