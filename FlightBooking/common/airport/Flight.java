package airport;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Voo individual, pronto para ser realizado, ou já realizado.
 */
public class Flight {
    // TODO: Lock;
    public final UUID id;
    public final Route route;
    public final LocalDate date;
    private int remainingCapacity;
    // private final Set<UUID> clients; // Association each client to the reservation code of his flight.
    // private final Set<Reservation> reservations; // Association each client to the reservation code of his flight.

    public Flight(UUID id, Route route, LocalDate date) {
        this.id = id;
        this.route = route;
        this.date = date;
        this.remainingCapacity = route.capacity;
    }

    public void addCapacity() {
        remainingCapacity--;
    }

    public void removeCapacity() {
        //if (remainingCapacity == 0) throw Cenas;
        remainingCapacity++;
    }
}
