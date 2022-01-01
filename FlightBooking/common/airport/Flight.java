package airport;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a flight.
 */
public class Flight {
    // TODO: Lock;
    public final UUID id;
    public final airport.Route route;
    public final LocalDate date;

    // This is necessary because when a flight is canceled, we need
    // to know all reservations associated to cancel then.
    // Like, if a connection flight is canceled, we need to cancel the flights associated to that connection
    private final Set<UUID> reservations; // Association each reservation code to the flight.

    /**
     * Constructor of the flight.
     * @param id the id.
     * @param route the route.
     * @param date the date.
     */
    public Flight(UUID id, airport.Route route, LocalDate date) {
        this.id = id;
        this.route = route;
        this.date = date;
        this.reservations = new HashSet<>();
    }

    /**
     * Adds a reservation to this flight.
     *
     * @param reservationId the id of the reservation.
     */
    public void addReservation(UUID reservationId) {
        this.reservations.add(reservationId);
    }

    /**
     * Removes a reservation to this flight.
     *
     * @param reservationId the id of the reservation.
     */
    public void removeReservation(UUID reservationId) {
        this.reservations.remove(reservationId);
    }
}
