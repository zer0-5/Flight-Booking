package airport;

import java.util.Set;
import java.util.UUID;

/**
 * Represents the reservation of one or multiple flights.
 */
public class Reservation {

    /**
     * Reservation code or id of this reservation.
     */
    public final UUID reservationCode;

    /**
     * Id of the client that owns this reservation.
     */
    public final UUID client;

    /**
     * Flights with the connections of the reservation.
     * E.g. Lisbon -> Toquio -> London
     */
    public final Set<UUID> flights;
    // TODO: Adicionar capacidade aos flights depois de apagar a reservation

    /**
     * Constructor
     *
     * @param reservationCode a reservation code.
     * @param client          a client.
     * @param flights         a set of flight's id.
     */
    public Reservation(UUID reservationCode, UUID client, Set<UUID> flights) {
        this.reservationCode = reservationCode;
        this.client = client;
        this.flights = flights;
    }
}
