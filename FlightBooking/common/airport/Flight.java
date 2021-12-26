package airport;

import java.time.LocalDate;
import java.util.*;

/**
 * Voo individual, pronto para ser realizado, ou já realizado.
 */
public class Flight {
  public final UUID id;
  public final Route route;
  public final LocalDate date;
  //private final Set<UUID> clients; // Association each client to the reservation code of his flight.
  private final Set<Reservation> reservations; // Association each client to the reservation code of his flight.

  public Flight(UUID id, Route connection, LocalDate date) {
    this.id = id;
    this.route = connection;
    this.date = date;
    this.reservations = new HashSet<>();
  }

  public void removeReservation(Reservation toRemove){
    reservations.remove(toRemove);
  }

  // TODO CLONE??
  public Set<Reservation> getReservations() {
    return reservations;
  }
}
