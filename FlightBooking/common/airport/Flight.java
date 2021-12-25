package common.airport;

import java.time.LocalDate;
import java.util.*;

/**
 * Voo individual, pronto para ser realizado, ou já realizado.
 */
public class Flight {
  private final UUID id;
  private final Connection connection;
  private final LocalDate date;
  //private final Set<UUID> clients; // Association each client to the reservation code of his flight.
  private final Set<Reservation> reservations; // Association each client to the reservation code of his flight.


  public Flight(UUID id, Connection connection, LocalDate date) {
    this.id = id;
    this.connection = connection;
    this.date = date;
    this.reservations = new HashSet<>();
  }

  public void removeReservation(Reservation toRemove){
    reservations.remove(toRemove);
  }
}
