import airport.Flight;
import airport.Reservation;
import airport.Route;

import java.time.LocalDate;
import java.util.*;

public class AirportSystem {
    private final Map<String, Set<Route>> connectionsByCityOrig;

    private final Map<UUID, Flight> flightsById;
    private final Map<LocalDate, Set<Flight>> flightsByDate;

    private final Set<LocalDate> canceledDays;

    //Key -> clientId
    private final Map<UUID, Set<Reservation>> reservationsByIdClient;

    public AirportSystem() {
        this.connectionsByCityOrig = new HashMap<>();
        this.flightsById = new HashMap<>();
        this.flightsByDate = new HashMap<>();
        this.canceledDays = new HashSet<>();
        this.reservationsByIdClient = new HashMap<>();
    }

    /**
     * Method to add a connection between two cities, with a given capacity.
     *
     * @param orig     the origin city.
     * @param dest     the destiny city.
     * @param capacity the capacity of each flight.
     */
    public void addRoute(String orig, String dest, int capacity) {
        Route newConn = new Route(orig, dest, capacity);
        if (connectionsByCityOrig.containsKey(orig)) connectionsByCityOrig.get(orig).add(newConn);
        else {
            Set<Route> toInsert = new HashSet<>();
            toInsert.add(new Route(orig, dest, capacity));
            connectionsByCityOrig.put(orig, toInsert);
        }
    }

    public boolean cancelOneSeat(UUID ticket, UUID client) {
        Flight flight = flightsById.remove(ticket);
        if (flight == null) return false;
        else {
            this.flightsByDate.remove(flight.date);
            Set<Reservation> reservations = reservationsByIdClient.get(client);
            if (reservations != null) {
                reservations.removeIf(elem -> elem.reserveCode.equals(ticket));
                removeReservation(ticket, client);
                return true;
            } else return false;
        }
    }

    private void removeReservation(UUID reservation, UUID client) {
        Set<Reservation> reservations = this.reservationsByIdClient.get(client);
        if (reservations != null) reservations.removeIf(elem -> elem.reserveCode.equals(reservation));
    }

    public void cancelFlightsByDate(LocalDate date) {

    }
}
