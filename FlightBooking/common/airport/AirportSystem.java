package common.airport;

import java.time.LocalDate;
import java.util.*;

public class AirportSystem {
  private final Map<String, Set<Connection>> connectionsByCityOrig;

  private final Map<UUID, Flight> flightsById;
  private final Map<LocalDate, Set<Flight>> FlightsByDate;

  private final Set<LocalDate> canceledDays;

  //private final Map<UUID, Reservation> reservationsById;

  //Key -> clientId
  private final Map<UUID, Set<Reservation>> reservationsByIdClient;

  /**
   * Method to add a connection between two cities, with a given capacity.
   * The else case
   * @param orig the origin city.
   * @param dest the destiny city.
   * @param capacity the capacity of each flight.
   */
  public void addConnection(String orig, String dest, int capacity){
    Connection newConn = new Connection(orig, dest, capacity);
    if (connectionsByCityOrig.containsKey(orig))
            connectionsByCityOrig.get(orig).add(newConn);
    else {
        Set<Connection> toInsert = new HashSet<>();
        connectionsByCityOrig.put(orig, toInsert.add((Connection) toInsert));
    }
  }

//Só um exemplo de como se usa a reserva e assim
  public void cancelOneSeat(UUID ticket, UUID client){
        Set<Reservation> reservations = reservationsByIdClient.get(client);
        Reservation toCancel = reservations.stream().filter(x -> x.getReserveCode().equals(ticket)).toList().get(0);
        UUID fligth = toCancel.getFlight();
        flightsById.get(fligth).removeReservation(toCancel);
  }



}
