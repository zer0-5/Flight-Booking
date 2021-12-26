package airport;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Voo individual, pronto para ser realizado, ou já realizado.
 */
public class Flight {
  // TODO: Lock;
  public final UUID id;
  public final Route route;
  public final LocalDate date;
  //private int remainingCapacity;
  // private final Set<UUID> clients; // Association each client to the reservation code of his flight.

  // This is necessary because when a flight is canceled, we need
  // to know all reservations associated to cancel then.
  // Like, if a connection flight is canceled, we need to cancel the flights associated to that connection
  private final Set<UUID> reservations; // Association each reservation code to the flight.

  public Flight(UUID id, Route route, LocalDate date) {
    this.id = id;
    this.route = route;
    this.date = date;
    //this.remainingCapacity = route.capacity;
    this.reservations = new HashSet<>();
  }

  public void addCapacity(UUID uuid) {
    //remainingCapacity--;
    this.reservations.add(uuid);
  }

  public void removeCapacity(UUID uuid) {
    //if (remainingCapacity == 0) throw Cenas;
    //remainingCapacity++;
    this.reservations.remove(uuid);
  }
}
